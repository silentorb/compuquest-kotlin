package compuquest.definition

import compuquest.simulation.definition.FactionNames
import compuquest.simulation.general.CharacterDefinition
import silentorb.mythic.ent.KeyTable

object Characters {
  val skeleton = "skeleton"
}

fun characterDefinitions(): KeyTable<CharacterDefinition> = mapOf(
  Characters.skeleton to CharacterDefinition(
    name = "skeleton",
    depiction = "skeleton",
    faction = FactionNames.undead,
    health = 50,
  )
)
