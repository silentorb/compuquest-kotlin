package compuquest.clienting.input

//import compuquest.simulation.input.Commands
//import godot.InputEvent
//import godot.InputEventJoypadButton
//import godot.InputMap
//import silentorb.mythic.haft.*

//val uiBindingMap: Map<String, String> = mapOf(
//	Commands.activate to "ui_accept",
//	Commands.menuBack to "ui_cancel",
//	Commands.moveLeft to "ui_left",
//	Commands.moveRight to "ui_right",
//	Commands.moveUp to "ui_up",
//	Commands.moveDown to "ui_down",
//)
//
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
