package silentorb.mythic.haft

import compuquest.clienting.input.getPlayerProfile
import godot.Input
import silentorb.mythic.ent.Id

const val gamepadEnumerationInterval = 2f

typealias Gamepads = List<Int>

var gamepadTimer: Float = 0f

fun updateGamepads(delta: Float, gamepads: Gamepads): Gamepads {
	gamepadTimer -= delta
	return if (gamepadTimer <= 0) {
		gamepadTimer = gamepadEnumerationInterval
		Input.getConnectedJoypads().mapNotNull { (it as? Long)?.toInt() }
	} else
		gamepads
}

fun updatePlayerGamepads(gamepads: Gamepads, players: PlayerMap, state: InputState): Map<Int, Int> =
	if (state.playerGamepads.none() && gamepads.any() && players.any() && getPlayerProfile(state, 0)?.usesGamepad == true)
		mapOf(0 to gamepads.first())
	else
		state.playerGamepads

fun getPlayerGamepad(state: InputState, playerIndex: Int): Int {
	val index = state.playerGamepads[playerIndex]
	return if (index != null)
		state.gamepads.getOrElse(index) { -1 }
	else
		-1
}
