package silentorb.mythic.haft

object InputDevices {
	const val keyboard = 0
	const val mouse = 1
	const val gamepad = 2
}

data class Binding(
	val device: Int,
	val scancode: Int,
	val command: String,
)

typealias Bindings = List<Binding>

fun createBindings(device: Int, bindings: Map<Int, String>) =
	bindings.map { Binding(device, it.key, it.value) }
