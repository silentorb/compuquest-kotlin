package scripts.world

import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty

@RegisterClass
class BlockNode : Spatial() {
	@Export
	@RegisterProperty
	var isUnique: Boolean = false
}
