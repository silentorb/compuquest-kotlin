package silentorb.mythic.haft

import com.fasterxml.jackson.annotation.JsonInclude
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key

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
	@JsonInclude(JsonInclude.Include.NON_DEFAULT) val argument: Any? = null,
	@JsonInclude(JsonInclude.Include.NON_DEFAULT) val processors: List<InputProcessor> = listOf()
)

typealias Bindings = List<Binding>

data class AdvancedCommand(
	val command: String,
	val argument: Any? = null,
	val processors: List<InputProcessor> = listOf(),
)

data class InputState(
	val profileOptions: HashedInputProfileOptionsMap,
	val profiles: InputProfileMap,
	val playerProfiles: List<Int>,
	val gamepads: Gamepads = listOf(),
	val playerGamepads: Map<Int, Int> = mapOf(),
	val playerInputContexts: Map<Id, Key> = mapOf(),
)

typealias PlayerMap = Map<Id, Int>
