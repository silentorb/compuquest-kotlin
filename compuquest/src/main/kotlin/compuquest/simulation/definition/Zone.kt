package compuquest.simulation.definition

import compuquest.simulation.general.Id

data class Zone(
  val name: String,
)

data class ZoneState(
  val owner: Id,
)
