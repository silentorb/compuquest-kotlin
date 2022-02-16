package compuquest.simulation.intellect.knowledge

import compuquest.simulation.general.Deck
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
	deck: Deck,
	bodyId: Id,
	body: Spatial,
	other: Id,
	range: Float
): Boolean {
	val otherBody = deck.bodies[other]
	val location = body.translation + Vector3(0f, 1f, 0f)

	return otherBody != null
			&& location.distanceTo(otherBody.translation + Vector3(0f, 1f, 0f)) <= range
			&& space.intersectRay(body.translation, otherBody.translation, variantArrayOf(deck.bodies[bodyId]!!, otherBody))
		.none()
}

fun getTargetRange(
	deck: Deck,
	body: Spatial,
	other: Id
): Float {
	val otherBody = deck.bodies[other]!!
	val location = body.translation + Vector3(0f, 1f, 0f)
	return location.distanceTo(otherBody.translation + Vector3(0f, 1f, 0f)).toFloat()
}

fun isVisible(
	world: World,
	body: Spatial,
	headLocation: Vector3,
	other: Id,
	visibilityRange: Float
): Boolean {
	val otherBody = world.deck.bodies[other]
	return otherBody != null &&
			body.globalTransform.origin.distanceTo(otherBody.globalTransform.origin) <= visibilityRange &&
			world.space.intersectRay(
				headLocation,
				otherBody.translation,
				variantArrayOf(body, otherBody),
				CollisionMasks.visibility.toLong()
			)
				.none()
}
