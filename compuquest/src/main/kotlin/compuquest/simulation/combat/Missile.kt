package compuquest.simulation.combat

import compuquest.clienting.audio.SpatialSound
import compuquest.clienting.audio.playSound
import compuquest.simulation.characters.Accessory
import compuquest.simulation.characters.AccessoryEffect
import compuquest.simulation.characters.noDuration
import compuquest.simulation.general.*
import compuquest.simulation.general.World
import godot.*
import godot.core.VariantArray
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.getCollisionShapeRadius
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent
import silentorb.mythic.timing.IntTimer
import kotlin.math.ceil

data class Missile(
	val velocity: Vector3,
	val origin: Vector3,
	val ignore: Long, // Id of Ignored body
	val diameter: Float,
	val source: Id,
	val damages: Damages,
	val sourceEffect: AccessoryEffect,
	val isSelfPropelled: Boolean,
	val timer: Int = noDuration,
) {
	val range: Float get() = sourceEffect.range
}

fun missileAttack(world: World, actor: Id, weapon: Accessory, targetLocation: Vector3?): Events {
	val deck = world.deck
	val definition = weapon.definition
	val effect = definition.actionEffects.first()
	val projectile = instantiateScene<Spatial>(effect.spawnsScene!!)!!
	val transform = applyEffectTransform(effect, getAttackerOriginAndFacing(deck, actor, targetLocation))
	projectile.transform = transform
	val isSelfPropelled = projectile !is RigidBody
	val shape = projectile.findNode("shape") as? CollisionShape
	return if (isSelfPropelled && shape == null)
		listOf()
	else {
		val velocity = -transform.basis.z * effect.speed
		val radius = if (shape != null)
			getCollisionShapeRadius(shape)
		else
			0f

		val sound = effect.sound

		if (!isSelfPropelled) {
			(projectile as RigidBody).linearVelocity = velocity
		}
		listOfNotNull(
			newHandEvent(
				Hand(
					components = listOf(
						projectile,
						Missile(
							velocity = velocity,
							source = actor,
							sourceEffect = effect,
							origin = transform.origin,
							diameter = radius * 2f,
							damages = newDamages(deck, actor, effect),
							ignore = projectile.getInstanceId(),
							isSelfPropelled = isSelfPropelled,
							timer = if (effect.duration > 0f) effect.durationInt else noDuration,
						),
					)
				)
			),
		)
	}
}

fun getMissilePostEvents(spawnOnEnd: String, location: Vector3) =
	if (spawnOnEnd.isNotEmpty()) {
		val postBody = instantiateScene<Spatial>(spawnOnEnd)
		if (postBody != null) {
			postBody.globalTransform {
				origin = location
			}
			listOf(
				newHandEvent(
					Hand(
						components = listOf(
							postBody,
							IntTimer(10)
						)
					)
				)
			)
		} else
			listOf()

	} else
		listOf()

fun updateMissile(world: World, actor: Id, missile: Missile, area: Area): Events {
	val origin = area.globalTransform.origin
	val collisions = (area.getOverlappingBodies() as VariantArray<Spatial>)
		.filter { (it as? Spatial)?.getInstanceId() != missile.ignore }
	val distanceTraveled = missile.origin.distanceTo(origin)
	val isFinished = collisions.any() || (missile.range > 0f && distanceTraveled > missile.range) || missile.timer == 0
	val deletions = if (isFinished) {
		listOf(newEvent(deleteEntityCommand, actor))
	} else
		listOf()

	val damageEvents = if (isFinished) {
		val postEvents = getMissilePostEvents(missile.sourceEffect.spawnOnEnd, origin)
		postEvents + applyDamage(world, origin, missile.source, missile.damages, missile.sourceEffect, collisions)
	} else
		listOf()

	return deletions + damageEvents
}

fun eventsFromMissile(world: World, delta: Float): (Id, Missile) -> Events = { actor, missile ->
	val deck = world.deck
	val body = deck.bodies[actor]
	val area = body as? Area ?: body?.getChild(0) as? Area
	val ccdPadding = 1.1f
	if (body != null && area != null) {
		// Implement basic CCD since Godot doesn't support that for areas
		if (missile.isSelfPropelled) {
			val iterations = ceil(missile.velocity.length() * delta * missile.diameter * ccdPadding).toInt()
			var events: Events = listOf()
			val offset = missile.velocity * delta / iterations.toFloat()
			for (i in (0 until iterations)) {
				body.translation += offset
				events = updateMissile(world, actor, missile, area)
				if (events.any())
					break
			}
			events
		} else {
			// If the missile is propelled by physics then it can use Godot's built-in RigidBody CCD
			updateMissile(world, actor, missile, area)
		}
	} else
		listOf()
}

fun updateMissile(): (Missile) -> Missile =
	{ missile ->
		if (missile.timer > 0)
			missile.copy(
				timer = missile.timer - 1,
			)
		else
			missile
	}
