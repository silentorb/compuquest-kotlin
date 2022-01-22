package scripts.gui

import godot.Label
import godot.Node
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty

@RegisterClass
class Popup : Node() {

	@Export
	@RegisterProperty
	var title: String = ""

	@RegisterFunction
	override fun _process(delta: Double) {
		val label = findNode("title") as? Label
		label?.text = title
	}
}
