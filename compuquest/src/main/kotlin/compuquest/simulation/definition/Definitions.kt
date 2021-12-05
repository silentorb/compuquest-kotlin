package compuquest.simulation.definition

import compuquest.simulation.general.AccessoryDefinition
import compuquest.simulation.general.CharacterDefinition
import silentorb.mythic.ent.KeyTable

data class Definitions(
  val accessories: KeyTable<AccessoryDefinition>,
  val characters: KeyTable<CharacterDefinition>,
)
