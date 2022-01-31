package compuquest.simulation.intellect.navigation

import godot.CSGPrimitive
import godot.CollisionObject
import godot.Node
import godot.core.Vector3


fun toRecastVector3(value: Vector3) = floatArrayOf(value.x.toFloat(), value.z.toFloat(), value.y.toFloat())
fun fromRecastVector3(value: FloatArray) = Vector3(value[0], value[2], value[1])

fun isCollisionObject(node: Node): Boolean =
	when (node) {
		is CollisionObject -> true
		is CSGPrimitive -> node.useCollision
		else -> false
	}