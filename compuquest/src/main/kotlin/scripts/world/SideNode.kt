package scripts.world

import compuquest.generation.general.Direction
import godot.Engine
import godot.Spatial
import godot.annotation.*
import godot.core.Vector3

@Tool
@RegisterClass
class SideNode : Spatial() {
	enum class Sides {
		closed,
		traversable,
	}

	@Export
	@RegisterProperty
	var mine = Sides.traversable

	@Export
	@RegisterProperty
	var other = Sides.traversable

	@Export
	@RegisterProperty
	var isEssential: Boolean = false

	@Export
	@RegisterProperty
	var isGreedy: Boolean = false

	@Export
	@RegisterProperty
	var isTraversable: Boolean = true

	@Export
	@RegisterProperty
	var canMatchEssential: Boolean = false

	@Export
	@RegisterProperty
	var cell = Vector3.ZERO
		set(value) {
			field = value
			updateName()
		}

	@Export
	@RegisterProperty
	@EnumTypeHint
	var direction = Direction.east
		set(value) {
			field = value
			updateName()
		}

	@Export
	@RegisterProperty
	var cellHeight: Int = 1

	fun updateName() {
		if (Engine.editorHint) {
			name = "${cell.x.toInt()} ${cell.y.toInt()} ${cell.z.toInt()} $direction"
		}
	}
}
