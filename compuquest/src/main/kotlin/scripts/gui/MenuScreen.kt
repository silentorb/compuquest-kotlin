package scripts.gui

import compuquest.clienting.gui.GameContext
import compuquest.clienting.gui.MenuItem
import compuquest.clienting.gui.activateMenuItem
import godot.Button
import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global

@RegisterClass
class MenuScreen : Node() {

	var message: List<String> = listOf()
	var items: List<MenuItem<GameContext>> = listOf()

	// This function needs to be snake case because Godot-JVM is converting everything to snake case under the hood
	@RegisterFunction
	fun on_pressed(index: Int) {
		val world = Global.world
		if (world != null) {
			val item = items[index]
			val context = GameContext(
				world = world,
				actor = Global.getPlayer()!!.key
			)
			activateMenuItem(context, item)
		}
	}

	@RegisterFunction
	override fun _ready() {
		val itemsContainer = findNode("items")!!
		val messageLabel = findNode("message") as? Label
		messageLabel?.text = message.joinToString("\n\n")
		val context = GameContext(
			world = Global.world!!,
			actor = Global.getPlayer()!!.key,
		)
		items.forEachIndexed { index, option ->
			val button = Button()
			button.text = option.title
			val enabled = option.enabled
			button.disabled = !(enabled == null || enabled(context, null))
			button.connect("pressed", this, "on_pressed", variantArrayOf(index))
			itemsContainer.addChild(button)
		}
	}
}
