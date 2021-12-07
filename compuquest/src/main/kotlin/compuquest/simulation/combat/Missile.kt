package compuquest.simulation.combat

import compuquest.simulation.general.World
import compuquest.simulation.general.deleteEntityCommand
import godot.core.Vector3
import scripts.entities.Projectile
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

data class Missile(
	val velocity: Vector3,
	val damageRadius: Float = 0f, // 0f for no AOE
	val damageFalloff: Float = 0f, // Falloff Exponent
	val damage: Int,
//  val damages: List<DamageDefinition>
)

//fun updateMissile(world: World, events: Events): (Id, Missile) -> Missile = { actor, missile ->
//
//}

fun eventsFromMissile(world: World, delta: Float): (Id, Missile) -> Events = { actor, missile ->
	val body = world.bodies[actor] as? Projectile
	if (body != null) {
		body.translate(missile.velocity * delta)
		val collisions = body.getOverlappingBodies()
		val hit = collisions.any { it != body.ignore }
		if (hit || body.origin.distanceTo(body.transform.origin) > body.range) {
			if (!hit) {
				val k = 0
			}
			listOf(newEvent(deleteEntityCommand, actor))
		} else
			listOf()
	} else
		listOf()
}
