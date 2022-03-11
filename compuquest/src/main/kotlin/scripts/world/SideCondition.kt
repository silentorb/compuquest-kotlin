package scripts.world

import compuquest.generation.general.Direction
import godot.Engine
import godot.Spatial
import godot.annotation.*
import godot.core.Vector3
import godot.global.GD

@RegisterClass
class SideCondition : Spatial() {
	enum class Condition {
		sideIsEmpty,
		sideIsNotEmpty,
	}

	@Export
	@RegisterProperty
	var condition = Condition.sideIsEmpty

	@Export
	@RegisterProperty
	var cell = Vector3.ZERO

	@Export
	@RegisterProperty
	@EnumTypeHint
	var direction = Direction.east
}
