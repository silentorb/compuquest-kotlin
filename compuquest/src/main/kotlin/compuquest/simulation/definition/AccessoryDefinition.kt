package compuquest.simulation.definition

import compuquest.simulation.general.Key

data class AccessoryDefinition(
  val name: String,
  val range: Float = 0f,
  val cost: Cost? = null,
  val cooldown: Float = 0f,
  val spawns: Key? = null,
)
