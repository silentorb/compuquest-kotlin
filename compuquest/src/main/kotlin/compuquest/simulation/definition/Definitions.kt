package compuquest.simulation.definition

import compuquest.simulation.characters.CharacterDefinition
import compuquest.simulation.general.AccessoryDefinition
import silentorb.mythic.ent.KeyTable

data class Definitions(
  val accessories: KeyTable<AccessoryDefinition>,
  val characters: KeyTable<CharacterDefinition>,
)
