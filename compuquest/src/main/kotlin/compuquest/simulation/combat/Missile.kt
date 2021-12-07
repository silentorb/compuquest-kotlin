package compuquest.simulation.combat

import compuquest.simulation.general.*
import godot.Area
import godot.PhysicsBody
import godot.Spatial
import godot.core.VariantArray
import godot.core.Vector3
import scripts.entities.Projectile
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

data class Missile(
	val velocity: Vector3,
	val origin: Vector3,
	val ignore: Long, // Id of Ignored body
	val range: Float,
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
		val effect = definition.effects.first()
		val projectile = instantiateScene<Spatial>(effect.spawns!!)!!
		projectile.translation = origin
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
							ignore = projectile.getInstanceId(),
//              damages = weapon.damages
						),
					)
				)
			)
		)
	}
}

fun eventsFromMissile(world: World, delta: Float): (Id, Missile) -> Events = { actor, missile ->
	val body = world.bodies[actor] as? Area
	if (body != null) {
		body.translate(missile.velocity * delta)
		val collisions = (body.getOverlappingBodies() as VariantArray<PhysicsBody>)
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

		deletions + damages
	} else
		listOf()
}
