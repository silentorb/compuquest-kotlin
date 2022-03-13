package scripts.world

import compuquest.generation.general.Direction
import compuquest.population.Sides
import godot.Engine
import godot.Spatial
import godot.annotation.*
import godot.core.Vector3
import godot.global.GD

@RegisterClass
class SideNode : Spatial() {

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
	var canMatchEssential: Boolean = true

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

	@RegisterFunction
	override fun _process(delta: Double) {
		if (Engine.editorHint) {
			GD.print("Hello")
		}
	}
}
