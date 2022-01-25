package scripts.gui

import compuquest.clienting.gui.*
import godot.Button
import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import silentorb.mythic.ent.Id

@RegisterClass
class MenuScreen : Node() {

	var title: String = ""
	var message: List<String> = listOf()
	var items: List<MenuItem<GameContext>> = listOf()
	var actor: Id = 0L

	@RegisterFunction
	fun on_pressed(index: Int) {
		val context = getGameContext(actor)
		if (context != null) {
			val item = items[index]
			activateMenuItem(context, actor, item)
		}
	}

	@RegisterFunction
	override fun _ready() {
		if (actor == 0L)
			throw Error("MenuScreen.actor is not initialized")

		val itemsContainer = findNode("items")!!
		val messageLabel = findNode("message") as? Label
		val titleLabel = findNode("title") as? Label
		titleLabel?.text = title
		messageLabel?.text = message.joinToString("\n\n")
		val context = getGameContext(actor)!!
		val buttons = items.mapIndexed { index, item ->
			val enabled = item.enabled
			val button = Button()
			button.text = getMenuItemTitle(context, item)
			button.name = "button"
			button.disabled = !(enabled == null || enabled(context, null))
			button.connect("pressed", this, "on_pressed", variantArrayOf(index))
			itemsContainer.addChild(button)
			button
		}

		buttons.firstOrNull()?.grabFocus()
	//		if (buttons.size > 1) {
//			buttons.first().getPath()
//			buttons.last().getPath()
//				buttons.last().focusNeighbourBottom = buttons.first().getPath()
//			buttons.first().focusNeighbourTop = buttons.last().getPath()
//		}
//		buttons.dropLast(1).forEachIndexed { index, button ->
//			button.focusNeighbourBottom =
//				buttons[index + 1].getPath()
//		}
	}
}
