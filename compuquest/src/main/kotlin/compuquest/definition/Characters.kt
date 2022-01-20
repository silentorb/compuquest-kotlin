package compuquest.definition

import compuquest.simulation.characters.CharacterDefinition
import compuquest.simulation.definition.Factions
import silentorb.mythic.ent.KeyTable

object Characters {
  const val player = "player"
  const val skeleton = "skeleton"
  const val viking = "viking"
}

fun characterDefinitions(): KeyTable<CharacterDefinition> = mapOf(
  Characters.player to CharacterDefinition(
    name = "Player",
    depiction = "viking",
    corpseDecay = 0f,
    faction = Factions.player,
    health = 100,
    accessories = listOf(
      Accessories.rifle,
    ),
  ),
  Characters.skeleton to CharacterDefinition(
    name = Characters.skeleton,
    depiction = "skeleton",
    faction = Factions.undead,
    health = 50,
    accessories = listOf(
      Accessories.rocketLauncher,
    ),
  ),
  Characters.viking to CharacterDefinition(
    name = Characters.viking,
    depiction = "viking",
    faction = Factions.undead,
    health = 50,
    accessories = listOf(
      Accessories.rocketLauncher,
    ),
  ),
)
