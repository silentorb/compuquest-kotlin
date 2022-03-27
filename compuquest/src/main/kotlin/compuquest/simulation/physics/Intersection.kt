package compuquest.simulation.physics

import godot.PhysicsDirectSpaceState
import godot.PhysicsShapeQueryParameters
import godot.Spatial
import godot.SphereShape
import godot.core.Dictionary
import godot.core.Transform
import godot.core.Vector3

fun intersectsSphere(space: PhysicsDirectSpaceState, origin: Vector3, radius: Float): List<Spatial> {
	val params = PhysicsShapeQueryParameters()
	params.collisionMask = CollisionMasks.dynamic.toLong()
	params.transform = Transform().translated(origin)
	val shape = SphereShape()
	shape.radius = radius.toDouble()
	params.shapeRid = shape.getRid()
	val result = space.intersectShape(params)
	return result.mapNotNull { hit ->
		val entry = (hit as Dictionary<Any?, Any?>).toMap()
		entry["collider"] as? Spatial
	}
}
