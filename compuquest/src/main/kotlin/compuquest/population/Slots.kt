package compuquest.population

import godot.Node
import godot.Spatial
import godot.core.Transform
import silentorb.mythic.godoting.findChildrenOfScriptType
import silentorb.mythic.godoting.getString


object SlotOrientation {
	const val ground = "ground"
	const val wall = "wall"
}

data class Slot(
	val orientation: String,
	val attributes: Set<String>,
	val transform: Transform,
)

typealias Slots = List<Slot>
typealias Transforms = List<Transform>

fun gatherSlots(scene: Node, attributes: Collection<String> = listOf()): Slots =
	findChildrenOfScriptType("res://entities/world/Slot.gd", scene)
		.filterIsInstance<Spatial>()
		.map { slotNode ->
			Slot(
				orientation = getString(slotNode, "orientation"),
				attributes = setOf(),
				transform = slotNode.globalTransform
			)
		}

//fun mapSlotType(slot: Slot): String =
//    if (slot.attributes.contains(SlotTypes.ground))
//      DistAttributes.floor
//    else
//      DistAttributes.wall
