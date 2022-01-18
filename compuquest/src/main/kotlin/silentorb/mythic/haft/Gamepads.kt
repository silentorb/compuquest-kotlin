package silentorb.mythic.haft

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

fun updatePlayerGamepads(gamepads: Gamepads, players: Collection<Id>, playerGamepads: Map<Id, Int>): Map<Id, Int> =
	if (playerGamepads.none() && gamepads.any() && players.any())
		mapOf(players.first() to gamepads.first())
	else
		playerGamepads

fun getPlayerGamepad(state: InputState, player: Id): Int {
	val index = state.playerGamepads[player]
	return if (index != null)
		state.gamepads.getOrElse(index) { -1 }
	else
		-1
}
