package compuquest.simulation.general

data class Command(
  val type: Key,
  val target: Any? = null,
  val value: Any? = null,
)
