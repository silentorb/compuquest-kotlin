package compuquest.simulation.combat

import compuquest.simulation.characters.Accessory
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
import kotlin.math.ceil

data class Missile(
	val velocity: Vector3,
	val origin: Vector3,
	val ignore: Long, // Id of Ignored body
	val range: Float,
	val diameter: Float,
	val damageRadius: Float = 0f, // 0f for no AOE
	val damageFalloff: Float = 0f, // Falloff Exponent
	val isSelfPropelled: Boolean,
	val damages: Damages,
	val timer: Int = noDuration,
)

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

		val damages = newDamages(deck, actor, effect)

		if (!isSelfPropelled) {
			(projectile as RigidBody).linearVelocity = velocity
		}
		listOf(
			newHandEvent(
				Hand(
					components = listOf(
						projectile,
						Missile(
							velocity = velocity,
							damages = damages,
							origin = transform.origin,
							range = effect.range,
							diameter = radius * 2f,
							ignore = projectile.getInstanceId(),
							isSelfPropelled = isSelfPropelled,
							timer = if (effect.duration > 0f) effect.durationInt else noDuration,
						),
					)
				)
			)
		)
	}
}

fun updateMissile(deck: Deck, actor: Id, missile: Missile, area: Area): Events {
	val collisions = (area.getOverlappingBodies() as VariantArray<Spatial>)
		.filter { (it as? Spatial)?.getInstanceId() != missile.ignore }
	val hit = collisions.any()
	val distanceTraveled = missile.origin.distanceTo(area.globalTransform.origin)
	val deletions = if (hit || (missile.range > 0f && distanceTraveled > missile.range) || missile.timer == 0) {
		if (!hit) {
			val k = 0
		}
		listOf(newEvent(deleteEntityCommand, actor))
	} else
		listOf()

	val attackerLocation = deck.bodies[actor]?.globalTransform?.origin

	val damages = collisions
		.flatMap { collision ->
			val collisionId = getBodyEntityId(deck, collision)
			if (collisionId != null)
				applyDamage(deck, attackerLocation, collisionId, collision, missile.damages)
			else
				listOf()
		}.take(1)

	return deletions + damages
}


fun eventsFromMissile(deck: Deck, delta: Float): (Id, Missile) -> Events = { actor, missile ->
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
				events = updateMissile(deck, actor, missile, area)
				if (events.any())
					break
			}
			events
		} else {
			// If the missile is propelled by physics then it can use Godot's built-in RigidBody CCD
			updateMissile(deck, actor, missile, area)
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
