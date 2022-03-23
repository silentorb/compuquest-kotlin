package compuquest.simulation.combat

import compuquest.simulation.characters.Accessory
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
	val damage: Int,
//  val damages: List<DamageDefinition>
)

fun missileAttack(world: World, actor: Id, weapon: Accessory, targetLocation: Vector3?, targetEntity: Id?): Events {
	val (origin, velocity) = getAttackerOriginAndFacing(world, actor, targetLocation, targetEntity, 0.8f)
	return if (origin != null) {
		val deck = world.deck
		val definition = weapon.definition
		val effect = definition.actionEffects.first()
		val projectile = instantiateScene<Spatial>(effect.spawnsScene!!)!!
		projectile.translation = origin
		val shape = projectile.findNode("shape") as? CollisionShape
		if (shape == null)
			listOf()
		else {
			val radius = getCollisionShapeRadius(shape)
			val characterBody = deck.bodies[actor]!!.getInstanceId()
			val damage = calculateDamage(deck, actor, weapon, effect)
			listOf(
				newHandEvent(
					Hand(
						components = listOf(
							projectile,
							Missile(
								velocity = velocity * effect.speed,
								damage = damage,
								origin = origin,
								range = definition.range,
								diameter = radius * 2f,
								ignore = characterBody,
							),
						)
					)
				)
			)
		}
	} else
		listOf()
}

fun updateMissile(deck: Deck, actor: Id, missile: Missile, body: Area, offset: Vector3): Events {
	body.translate(offset)
	val collisions = (body.getOverlappingBodies() as VariantArray<Spatial>)
		.filter { (it as? Spatial)?.getInstanceId() != missile.ignore }
	val hit = collisions.any()
	val distanceTraveled = missile.origin.distanceTo(body.transform.origin)
	val deletions = if (hit || distanceTraveled > missile.range) {
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
				applyDamage(deck, actor, attackerLocation, collisionId, collision, missile.damage)
			else
				listOf()
		}.take(1)

	return deletions + damages
}

fun eventsFromMissile(deck: Deck, delta: Float): (Id, Missile) -> Events = { actor, missile ->
	val body = deck.bodies[actor] as? Area
	val ccdPadding = 1.1f
	if (body != null) {
		val iterations = ceil(missile.velocity.length() * delta * missile.diameter * ccdPadding).toInt()
		var events: Events = listOf()
		val offset = missile.velocity * delta / iterations.toFloat()
		for (i in (0 until iterations)) {
			events = updateMissile(deck, actor, missile, body, offset)
			if (events.any())
				break
		}
		events
	} else
		listOf()
}
