package compuquest.definition

import compuquest.simulation.characters.CharacterDefinition
import compuquest.simulation.definition.FactionNames
import silentorb.mythic.ent.KeyTable

object Characters {
  val player = "player"
  val skeleton = "skeleton"
  val viking = "viking"
}

fun characterDefinitions(): KeyTable<CharacterDefinition> = mapOf(
  Characters.player to CharacterDefinition(
    name = Characters.player,
    depiction = "viking",
    corpseDecay = 0f,
    faction = FactionNames.player,
    health = 100,
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
  Characters.viking to CharacterDefinition(
    name = Characters.viking,
    depiction = "viking",
    faction = FactionNames.undead,
    health = 50,
    accessories = listOf(
      Accessories.rocketLauncher,
    ),
  ),
)
