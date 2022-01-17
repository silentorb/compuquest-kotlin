package compuquest.simulation.intellect

import compuquest.simulation.general.Body
import compuquest.simulation.general.World
import compuquest.simulation.physics.CollisionMasks
import godot.PhysicsDirectSpaceState
import godot.Spatial
import godot.core.Vector3
import godot.core.variantArrayOf
import silentorb.mythic.ent.Id

const val spiritRangeBuffer = 0.1f

fun inRangeAndVisible(
	space: PhysicsDirectSpaceState,
	world: World,
	bodyId: Id,
	body: Body,
	other: Id,
	range: Float
): Boolean {
	val otherBody = world.bodies[other]
	val location = body.translation + Vector3(0f, 1f, 0f)

	return otherBody != null
			&& location.distanceTo(otherBody.translation + Vector3(0f, 1f, 0f)) <= range
			&& space.intersectRay(body.translation, otherBody.translation, variantArrayOf(world.bodies[bodyId]!!, otherBody))
		.none()
}

fun getTargetRange(
	world: World,
	body: Body,
	other: Id
): Float {
	val otherBody = world.bodies[other]!!
	val location = body.translation + Vector3(0f, 1f, 0f)
	return location.distanceTo(otherBody.translation + Vector3(0f, 1f, 0f)).toFloat()
}

fun isVisible(
	world: World,
	body: Spatial,
	headLocation: Vector3,
	other: Id
): Boolean {
	val otherBody = world.bodies[other]
	return otherBody != null &&
			world.space.intersectRay(headLocation, otherBody.translation, variantArrayOf(body, otherBody), CollisionMasks.visibility.toLong())
				.none()
}
