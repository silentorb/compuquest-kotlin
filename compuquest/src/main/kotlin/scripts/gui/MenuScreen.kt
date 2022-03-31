package scripts.gui

import compuquest.clienting.gui.*
import compuquest.clienting.input.uiMenuNavigationBindingMap
import compuquest.simulation.input.Commands
import godot.Button
import godot.Control
import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.haft.Bindings
import silentorb.mythic.haft.RelativeButtonState
import silentorb.mythic.haft.getButtonState
import silentorb.mythic.haft.isButtonJustReleased

@RegisterClass
class MenuScreen : Node(), HasCustomFocus {

	var title: String = ""
	var message: List<String> = listOf()
	var items: List<MenuItem<GameContext>> = listOf()
	var itemsContainer: Node? = null
	var actor: Id = 0L
	var focusIndex = 0

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

		// Menus should not be loaded if there is no client so don't worry about null checking.
		// If client is null here than there are larger problems to deal with.
		val client = Global.instance!!.client!!

		val focusMode = getFocusMode(client.players.size)
		itemsContainer = findNode("items")
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
//			if (focusMode == FocusMode.custom) {
//				button.focusMode = Control.FocusMode.FOCUS_NONE.id
//			}
			itemsContainer?.addChild(button)
			button
		}

		buttons.firstOrNull()?.grabFocus()
	}

	override fun updateFocus(bindings: Bindings, gamepad: Int) {
		val itemCount = items.size
		val newIndex = uiMenuNavigationBindingMap.keys.fold(focusIndex) { index, command ->
			val state = getButtonState(bindings, gamepad, command)
			if (state == RelativeButtonState.justReleased) {
				when (command) {
					Commands.moveUp -> wrapIndex(itemCount, index - 1)
					Commands.moveDown -> wrapIndex(itemCount, index + 1)
					else -> index
				}
			} else
				index
		}

		if (newIndex != focusIndex) {
			(itemsContainer?.getChild(newIndex.toLong()) as? Control)?.grabFocus()
			focusIndex = newIndex
		}

		if (isButtonJustReleased(bindings, gamepad, Commands.activate)) {
			on_pressed(newIndex)
		}
	}
}
