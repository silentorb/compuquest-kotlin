package compuquest.clienting.input

import compuquest.clienting.Client
import compuquest.clienting.gui.MenuStacks
import compuquest.simulation.input.Commands
import godot.*
import scripts.Global
import silentorb.mythic.haft.*
import silentorb.mythic.happening.newEvent

val uiBindingMap: Map<String, String> = mapOf(
	Commands.activate to "ui_accept",
	Commands.menuBack to "ui_cancel",
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
					if (isButtonJustPressed(bindings, gamepad, command)) {
//						when (command) {
//							Commands.activate -> {
//								val focused = Global.instance!!.staticControl.getFocusOwner()
//								if (focused != null) {
//									when (focused) {
//										is Button -> focused._pressed()
//									}
//								}
//							}
//							else -> {
						val event = InputEventAction()
						event.action = godotCommand
						event.pressed = true
						Input.parseInputEvent(event)
					}
//						}
				}
			}
		}
	}
}
}

//fun SyncGodotUiBindings(input: InputState) {
//	for ((_, godotCommand) in uiBindingMap) {
//		InputMap.actionEraseEvents(godotCommand)
//	}
//	input.playerProfiles.forEachIndexed { index, profileId ->
//		val profile = input.profiles[profileId]
//		if (profile != null) {
//			val gamepad = getPlayerGamepad(input, index)
//			val bindings = profile.bindings[InputContexts.ui] ?: listOf()
//			for ((command, godotCommand) in uiBindingMap) {
//				for (binding in bindings.filter { it.command == command }) {
//					val event = bindingToGodotInputEvent(binding, gamepad)
//					if (event != null) {
//						InputMap.actionAddEvent(godotCommand, event)
//					}
//				}
//			}
//		}
//	}
//}
