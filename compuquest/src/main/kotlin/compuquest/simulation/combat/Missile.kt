package compuquest.simulation.combat

import compuquest.simulation.general.*
import compuquest.simulation.general.World
import godot.*
import godot.core.VariantArray
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Event
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
	val originAndFacing = getAttackerOriginAndFacing(world, actor, targetLocation, targetEntity, 0.8f)
	return if (originAndFacing == null)
		listOf()
	else {
		val (origin, velocity) = originAndFacing
		val definition = weapon.definition
		val effect = definition.actionEffects.first()
		val projectile = instantiateScene<Spatial>(effect.spawns!!)!!
		projectile.translation = origin
		val shape = projectile.findNode("collisionShape") as? CollisionShape
		val radius = (shape?.shape as? SphereShape)?.radius?.toFloat() ?: 0.1f
		val characterBody = world.bodies[actor]!!.getInstanceId()
//		projectile.ignore = world.bodies[actor]
//    projectile.addCollisionExceptionWith(world.bodies[actor]!!)
//    projectile.applyCentralImpulse(velocity * effect.speed)
//    world.scene!!.addChild(projectile)

		listOf(
			newHandEvent(
				Hand(
					components = listOf(
						projectile,
//////            Body(
//////              position = origin,
//////              velocity = vector * weapon.velocity,
//////              scale = Vector3(0.5f)
//////            ),
						Missile(
							velocity = velocity * effect.speed,
							damage = effect.strengthInt,
							origin = origin,
							range = definition.range,
							diameter = radius * 2f,
							ignore = characterBody,
//              damages = weapon.damages
						),
					)
				)
			)
		)
	}
}

fun updateMissile(world: World, actor: Id, missile: Missile, body: Area, offset: Vector3): Events {
	body.translate(offset)
	val collisions = (body.getOverlappingBodies() as VariantArray<Spatial>)
		.filter { (it as? Spatial)?.getInstanceId() != missile.ignore }
	val hit = collisions.any()
	val deletions = if (hit || missile.origin.distanceTo(body.transform.origin) > missile.range) {
		if (!hit) {
			val k = 0
		}
		listOf(newEvent(deleteEntityCommand, actor))
	} else
		listOf()

	val damages = collisions.mapNotNull { collision ->
		val collisionId = world.bodies.entries.firstOrNull { it.value == collision }?.key
		if (world.deck.characters.containsKey(collisionId))
			Event(damageEvent, collisionId, missile.damage)
		else
			null
	}.take(1)
	return deletions + damages
}

fun eventsFromMissile(world: World, delta: Float): (Id, Missile) -> Events = { actor, missile ->
	val body = world.bodies[actor] as? Area
	val ccdPadding = 1.1f
	if (body != null) {
		val iterations = ceil(missile.velocity.length() * delta * missile.diameter * ccdPadding).toInt()
		var events: Events = listOf()
		val offset = missile.velocity * delta / iterations.toFloat()
		for (i in (0 until iterations)) {
			events = updateMissile(world, actor, missile, body, offset)
			if (events.any())
				break
		}
		events
	} else
		listOf()
}
