package compuquest.clienting.input

import compuquest.clienting.Client
import compuquest.clienting.gui.MenuStacks
import godot.Input
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.haft.InputProfileOptions
import silentorb.mythic.haft.InputProfileOptionsMap

object DefaultInputProfiles {
	const val keyboardMouse: Int = 1
	const val gamepad: Int = 2
	const val keyboardMouseGamepad: Int = 3
}

object InputContexts {
	const val game = "game"
	const val ui = "ui"
}

fun defaultInputProfiles(): InputProfileOptionsMap =
	mapOf(
		DefaultInputProfiles.keyboardMouse to InputProfileOptions(
			name = "Keyboard and Mouse",
			bindings = defaultKeyboardMouseInputBindings()
		),
		DefaultInputProfiles.gamepad to InputProfileOptions(
			name = "Gamepad",
			bindings = defaultGamepadInputBindings()
		),
		DefaultInputProfiles.keyboardMouseGamepad to InputProfileOptions(
			name = "Keyboard, Mouse, and Gamepad",
			references = listOf(
				DefaultInputProfiles.keyboardMouse,
				DefaultInputProfiles.gamepad,
			)
		),
	)

fun defaultPlayerInputProfiles() =
	listOf(
		DefaultInputProfiles.keyboardMouseGamepad,
		DefaultInputProfiles.gamepad,
		DefaultInputProfiles.gamepad,
		DefaultInputProfiles.gamepad,
	)

// Defaults to InputContexts.ui
fun getPlayerInputContext(menuStacks: MenuStacks, player: Id): Key =
	if (menuStacks[player]?.none() == true)
		InputContexts.game
	else
		InputContexts.ui

fun getMousePlayer(client: Client): Id? =
	client.playerMap
		.entries
		.firstOrNull { (id, index) ->
			val profile = getPlayerProfile(client.input, index)
			profile?.usesMouse == true
		}?.key

fun updateMouseMode(client: Client) {
	val mousePlayer = getMousePlayer(client)
	val mode = if (mousePlayer != null && getPlayerInputContext(client.menuStacks, mousePlayer) == InputContexts.game)
		Input.MOUSE_MODE_CAPTURED
	else
		Input.MOUSE_MODE_VISIBLE

	Input.setMouseMode(mode)
}
