package compuquest.simulation.definition

import compuquest.simulation.general.Key

data class Definitions(
  val accessories: Map<Key, AccessoryDefinition>,
  val zones: Map<Key, ZoneDefinition>,
)
