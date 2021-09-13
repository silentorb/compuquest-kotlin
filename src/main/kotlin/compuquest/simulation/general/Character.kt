package compuquest.simulation.general

data class Character(
  val name: String,
  val faction: Id,
  val depiction: Id,
  val health: IntResource,
)
