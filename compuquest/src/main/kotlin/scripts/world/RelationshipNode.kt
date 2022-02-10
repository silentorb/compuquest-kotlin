package scripts.world

import godot.Node
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.NodePath
import godot.core.Vector3

@RegisterClass
class RelationshipNode : Node() {

	@Export
	@RegisterProperty
	var entity: NodePath? = null

	@Export
	@RegisterProperty
	var isA: String = ""

	@Export
	@RegisterProperty
	var of: NodePath? = null
}
