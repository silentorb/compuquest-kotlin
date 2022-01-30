package compuquest.clienting.input

import compuquest.clienting.gui.MenuStacks
import compuquest.simulation.input.Commands
import godot.Input
import godot.InputEventAction
import silentorb.mythic.haft.*

val uiBindingMap: Map<String, String> = mapOf(
	Commands.activate to "ui_accept",
//	Commands.menuBack to "ui_cancel",
	Commands.moveLeft to "ui_left",
	Commands.moveRight to "ui_right",
	Commands.moveUp to "ui_up",
	Commands.moveDown to "ui_down",
)

fun syncGodotUiEvents(playerMap: PlayerMap, menuStacks: MenuStacks, input: InputState) {
	for ((player, playerIndex) in playerMap) {
		if (menuStacks[player]?.any() == true) {
			val bindings = getPlayerBindings(input, player, playerIndex)
			if (bindings != null) {
				val gamepad = getPlayerGamepad(input, playerIndex)
				for ((command, godotCommand) in uiBindingMap) {
					// TODO: Create a function getButtonPressedOrReleased that returns a three-state enum instead of two calls
					if (isButtonJustPressed(bindings, gamepad, command)) {
						val event = InputEventAction()
						event.action = godotCommand
						event.pressed = true
						Input.parseInputEvent(event)
					}
					else if (isButtonJustReleased(bindings, gamepad, command)) {
						val event = InputEventAction()
						event.action = godotCommand
						event.pressed = false
						Input.parseInputEvent(event)
					}
				}
			}
		}
	}
}
