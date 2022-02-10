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
class ScenarioNode : Node() {

	@Export
	@RegisterProperty
	var defaultPlayerFaction: String = ""
}
