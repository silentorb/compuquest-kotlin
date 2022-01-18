package silentorb.mythic.haft

import com.fasterxml.jackson.annotation.JsonInclude
import silentorb.mythic.ent.Id

object InputDevices {
	const val keyboard = 0
	const val mouse = 1
	const val gamepad = 2
}

enum class InputProcessorType {
	deadzone,
	invert,
	scale,
}

data class InputProcessor(
	val type: InputProcessorType,
	@JsonInclude(JsonInclude.Include.NON_DEFAULT) val float1: Float = 0f,
)

data class Binding(
	val device: Int,
	val scancode: Int,
	val command: String,
	@JsonInclude(JsonInclude.Include.NON_DEFAULT) val processors: List<InputProcessor> = listOf()
)

typealias Bindings = List<Binding>

data class CommandWithProcessors(
	val command: String,
	val processors: List<InputProcessor>,
)

data class InputProfile(
	val bindings: Bindings,
)

data class InputState(
	val profiles: Map<Int, InputProfile>,
	val playerProfiles: Map<Long, Int>,
	val gamepads: Gamepads = listOf(),
	val playerGamepads: Map<Id, Int> = mapOf(),
)
