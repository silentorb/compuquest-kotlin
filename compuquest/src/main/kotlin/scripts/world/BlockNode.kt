package scripts.world

import compuquest.generation.general.BlockRotations
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty

@RegisterClass
class BlockNode : Spatial() {
	@Export
	@RegisterProperty
	var isUnique: Boolean = false

	@Export
	@RegisterProperty
	var rotations = BlockRotations.none
}
