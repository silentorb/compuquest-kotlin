package compuquest.simulation.physics

import compuquest.simulation.combat.getToolTransform
import compuquest.simulation.general.World
import compuquest.simulation.general.getBodyEntityId
import godot.Spatial
import godot.core.Vector3
import godot.core.variantArrayOf
import silentorb.mythic.ent.Id

fun castRay(
	world: World,
	origin: Vector3,
	target: Vector3,
	vararg ignore: Spatial
): Pair<Spatial?, Vector3?> {
	val space = world.space
	val result = space.intersectRay(origin, target, variantArrayOf(*ignore)).toMap()
	val collider = result["collider"] as? Spatial
	val location = result["position"] as? Vector3
	return collider to location
}

// Eventually may need to return more information such as hit location but just the id is enough for now
fun castRay(world: World, actor: Id, maxDistance: Float): Pair<Spatial?, Vector3?> {
	val transform = getToolTransform(world.deck, actor)
	val body = world.deck.bodies[actor]
	return if (transform != null && body != null) {
		// Overshoot the ray and then later check against the distance to the object so that
		// rotating slightly won't miss against rounded targets that are barely in range.
		// In other words, aiming should follow the camera rotation arc and not the target body's arc
		val origin = transform.origin
		val target = origin + transform.basis.z * (maxDistance * 1.5)
		val (collider, location) = castRay(world, origin, target, body)
		return if (collider != null && location != null && getLocation(collider).distanceTo(origin) <= maxDistance)
			collider to location
		else
			null to null
	} else
		null to null
}

fun castRayForId(world: World, actor: Id, maxDistance: Float): Id? {
	val recipient = castRay(world, actor, maxDistance).first
	return if (recipient != null)
		getBodyEntityId(world.deck, recipient)
	else
		null
}

//fun castCharacterRay(world: World, actor: Id, maxDistance: Float): Id? {
//	val collider = castRay(world, actor, maxDistance)
//	return if (collider != null)
//		world.deck.bodies.entries
//			.firstOrNull { it.value == collider }
//			?.key
//	else
//		null
//}
