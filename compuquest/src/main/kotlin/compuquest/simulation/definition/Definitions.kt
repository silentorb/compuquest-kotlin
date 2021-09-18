package compuquest.simulation.definition

import silentorb.mythic.ent.Key

data class Definitions(
  val accessories: Map<Key, AccessoryDefinition>,
  val zones: Map<Key, ZoneDefinition>,
)
