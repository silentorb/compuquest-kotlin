package silentorb.mythic.haft

import silentorb.mythic.ent.Id

fun createBindings(device: Int, bindings: Map<Long, Any>) =
	bindings.map { (key, value) ->
		val command = when (value) {
			is String -> value
			is CommandWithProcessors -> value.command
			else -> throw Error("Invalid binding value")
		}
		val processors = if (value is CommandWithProcessors)
			value.processors
		else
			listOf()

		Binding(device, key.toInt(), command, processors)
	}

fun updateInput(delta: Float, players: Collection<Id>, state: InputState): InputState {
	val gamepads = updateGamepads(delta, state.gamepads)
	return state.copy(
		gamepads = gamepads,
		playerGamepads = updatePlayerGamepads(gamepads, players, state.playerGamepads)
	)
}
