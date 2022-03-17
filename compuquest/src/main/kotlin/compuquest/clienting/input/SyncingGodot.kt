package compuquest.clienting.input

import compuquest.clienting.gui.FocusMode
import compuquest.clienting.gui.MenuStacks
import compuquest.clienting.gui.getFocusMode
import compuquest.simulation.input.Commands
import godot.Input
import godot.InputEventAction
import silentorb.mythic.haft.*

val uiMenuNavigationBindingMap: Map<String, String> = mapOf(
	Commands.moveUp to "ui_up",
	Commands.moveDown to "ui_down",
)

val uiBindingMap: Map<String, String> = mapOf(
	Commands.activate to "ui_accept",
//	Commands.menuBack to "ui_cancel",
	Commands.moveLeft to "ui_left",
	Commands.moveRight to "ui_right",
) + uiMenuNavigationBindingMap

fun syncGodotUiEvents(playerMap: PlayerMap, menuStacks: MenuStacks, input: InputState) {
	for ((player, playerIndex) in playerMap) {
		if (getFocusMode(playerMap.size) == FocusMode.native) {
			if (menuStacks[player]?.any() == true) {
				val bindings = getPlayerBindings(input, player, playerIndex)
				if (bindings != null) {
					val gamepad = getPlayerGamepad(input, playerIndex)
					for ((command, godotCommand) in uiBindingMap) {
						val state = getButtonState(bindings, gamepad, command)
						if (state == RelativeButtonState.justReleased || state == RelativeButtonState.justPressed) {
							val event = InputEventAction()
							event.action = godotCommand
							event.pressed = state == RelativeButtonState.justPressed
							Input.parseInputEvent(event)
						}
					}
				}
			}
		}
	}
}
