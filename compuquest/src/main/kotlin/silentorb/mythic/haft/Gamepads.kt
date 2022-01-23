package silentorb.mythic.haft

import compuquest.clienting.input.getPlayerProfile
import godot.Input
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues
import silentorb.mythic.happening.filterEventsByType

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

const val setPlayerGamepad = "setPlayerGamepad" // target = playerIndex: Int, value = gamepad: Int

fun <T> mergePair(value: Pair<List<T>, List<T>>): List<T> =
	value.first + value.second

fun updatePlayerGamepads(gamepads: Gamepads, events: Events, players: List<Int>, state: InputState): Map<Int, Int> {
	val playersWithGamepads = players.filter { index -> getPlayerProfile(state, index)?.usesGamepad == true }
	val playerGamepads = state.playerGamepads
	val setPlayerGamepadEvents = filterEventValues<Int>(setPlayerGamepad, events)
	return if (gamepads != Cache.gamepads ||
		playerGamepads != Cache.playerGamepads ||
		playersWithGamepads != Cache.playersWithGamepads ||
		setPlayerGamepadEvents.any()
	) {
		Cache.gamepads = gamepads
		Cache.playerGamepads = playerGamepads
		Cache.playersWithGamepads = playersWithGamepads
		val pruned = playerGamepads
			.filter { (playerIndex, gamepad) ->
				playersWithGamepads.contains(playerIndex) && gamepads.contains(gamepad)
			}

		// Prioritize gamepads with associated setPlayerGamepad events
		val availableGamepads = mergePair(
			(gamepads - pruned.values)
				.partition { setPlayerGamepadEvents.contains(it) }
		)

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
