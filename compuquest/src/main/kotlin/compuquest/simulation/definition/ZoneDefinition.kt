package compuquest.simulation.definition

import silentorb.mythic.ent.Id

data class ZoneDefinition(
  val name: String,
)

data class Zone(
  val owner: Id,
  val name: String,
)
