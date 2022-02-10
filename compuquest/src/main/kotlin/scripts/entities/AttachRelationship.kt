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
class AttachRelationship : Node() {

	@Export
	@RegisterProperty
	var isA: String = ""

	@Export
	@RegisterProperty
	var of: NodePath? = null
}
