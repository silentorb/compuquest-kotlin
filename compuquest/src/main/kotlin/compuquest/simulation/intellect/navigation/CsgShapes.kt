package compuquest.simulation.intellect.navigation

import godot.BoxShape
import godot.CSGBox
import godot.CSGPrimitive
import godot.Shape
import godot.core.Vector3

fun getCsgShape(primitive: CSGPrimitive): Shape? =
	when (primitive) {
		is CSGBox -> {
			val shape = BoxShape()
			shape.extents = Vector3(
				primitive.width / 2,
				primitive.height / 2,
				primitive.depth / 2,
			)
			shape
		}
		else -> null
	}
