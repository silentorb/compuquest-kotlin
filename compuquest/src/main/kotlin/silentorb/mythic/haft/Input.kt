package silentorb.mythic.haft

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.firstEventByType

const val setPlayerInputProfiles = "setPlayerInputProfiles"

fun createBindings(device: Int, bindings: Map<Long, Any>) =
	bindings.map { (key, value) ->
		val command = when (value) {
			is String -> value
			is AdvancedCommand -> value.command
			else -> throw Error("Invalid binding value")
		}
		val argument = when (value) {
			is AdvancedCommand -> value.argument
			else -> null
		}
		val processors = if (value is AdvancedCommand)
			value.processors
		else
			listOf()

		Binding(
			device = device,
			scancode = key.toInt(),
			command = command,
			argument = argument,
			processors = processors,
		)
	}

fun updateInput(
	delta: Float,
	players: PlayerMap,
	playerInputContexts: Map<Id, Key>,
	events: Events,
	state: InputState
): InputState {
	val gamepads = updateGamepads(delta, state.gamepads)
	return state.copy(
		gamepads = gamepads,
		playerGamepads = updatePlayerGamepads(gamepads, players, state),
		playerInputContexts = playerInputContexts,
		playerProfiles = firstEventByType<List<Int>>(setPlayerInputProfiles, events)?.value ?: state.playerProfiles
	)
}
