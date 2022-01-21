package scripts.gui

import compuquest.clienting.gui.*
import godot.Button
import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global
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
		items.forEachIndexed { index, item ->
			val enabled = item.enabled
			val button = Button()
			button.text = getMenuItemTitle(context, item)
			button.name = "button"
			button.disabled = !(enabled == null || enabled(context, null))
			button.connect("pressed", this, "on_pressed", variantArrayOf(index))
			itemsContainer.addChild(button)
		}
	}
}
