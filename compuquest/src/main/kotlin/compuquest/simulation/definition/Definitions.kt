package compuquest.simulation.definition

import silentorb.mythic.ent.Key

data class Definitions(
  val zones: Map<Key, ZoneDefinition>,
)
