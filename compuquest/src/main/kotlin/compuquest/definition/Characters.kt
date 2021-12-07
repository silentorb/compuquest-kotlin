package compuquest.definition

import compuquest.simulation.characters.CharacterDefinition
import compuquest.simulation.definition.FactionNames
import silentorb.mythic.ent.KeyTable

object Characters {
  val player = "player"
  val skeleton = "skeleton"
}

fun characterDefinitions(): KeyTable<CharacterDefinition> = mapOf(
  Characters.player to CharacterDefinition(
    name = Characters.player,
    depiction = "skeleton",
    faction = FactionNames.neutral,
    health = 50,
    accessories = listOf(
      Accessories.rifle,
    ),
  ),
  Characters.skeleton to CharacterDefinition(
    name = Characters.skeleton,
    depiction = "skeleton",
    faction = FactionNames.undead,
    health = 50,
    accessories = listOf(
      Accessories.rocketLauncher,
    ),
  ),
)
