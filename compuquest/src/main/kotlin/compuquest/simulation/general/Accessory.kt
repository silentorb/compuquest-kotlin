package compuquest.simulation.general

data class Accessory(
  val type: Key,
  val owner: Id,
  val level: Int? = null,
  val cooldown: Float = 0f,
)
