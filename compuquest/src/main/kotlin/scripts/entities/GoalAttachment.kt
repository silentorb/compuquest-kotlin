package scripts.entities

import godot.Node
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.NodePath
import godot.core.Vector3

@RegisterClass
class GoalAttachment : Node() {

	@Export
	@RegisterProperty
	var destinationPath: NodePath? = null
	var destination: Vector3? = null

	@RegisterFunction
	override fun _ready() {
		destination = if (destinationPath != null)
			(getNode(destinationPath!!) as? Spatial)?.globalTransform?.origin
		else
			null
	}
}
