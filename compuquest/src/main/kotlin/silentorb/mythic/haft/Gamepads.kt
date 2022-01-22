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

private object Cache {
	var gamepads: Gamepads = listOf()
	var playerGamepads: Map<Int, Int> = mapOf()
	var playersWithGamepads: List<Int> = listOf()
}

fun updatePlayerGamepads(gamepads: Gamepads, players: PlayerMap, state: InputState): Map<Int, Int> {
	val playersWithGamepads = players.values.filter { index -> getPlayerProfile(state, index)?.usesGamepad == true }
	val playerGamepads = state.playerGamepads
	return if (gamepads != Cache.gamepads ||
		playerGamepads != Cache.playerGamepads ||
		playersWithGamepads != Cache.playersWithGamepads
	) {
		Cache.gamepads = gamepads
		Cache.playerGamepads = playerGamepads
		Cache.playersWithGamepads = playersWithGamepads
		val pruned = playerGamepads
			.filter { (playerIndex, gamepad) ->
				playersWithGamepads.contains(playerIndex) && gamepads.contains(gamepad)
			}

		val availableGamepads = gamepads - pruned.values
		val additions = playersWithGamepads
			.minus(pruned.keys)
			.sorted()
			.take(availableGamepads.size)
			.zip(availableGamepads) { a, b -> a to b }
			.associate { it }

		pruned + additions
	} else
		state.playerGamepads
}

fun getPlayerGamepad(state: InputState, playerIndex: Int): Int {
	val index = state.playerGamepads[playerIndex]
	return if (index != null)
		state.gamepads.getOrElse(index) { -1 }
	else
		-1
}
