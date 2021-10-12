package compuquest.clienting.input

data class Binding(
  val device: Int,
  val scancode: Int,
  val command: String,
)

data class InputProfile(
  val bindings: List<Binding>,
)

const val defaultInputProfile: Int = 1

data class InputOptions(
  val profiles: Map<Int, InputProfile> = mapOf(),
)
