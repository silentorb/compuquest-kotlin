package compuquest.simulation.definition

import compuquest.simulation.general.Key

data class Cost(
  val resource: Key,
  val amount: Int,
)
