package compuquest.definition

import compuquest.simulation.definition.AccessoryDefinition
import compuquest.simulation.definition.Cost
import compuquest.simulation.definition.ResourceType
import silentorb.mythic.ent.Key

fun defineAccessories(): Map<Key, AccessoryDefinition> = mapOf(
  "fireball" to AccessoryDefinition(
    name = "Fireball",
    range = 25f,
    cooldown = 2f,
    cost = Cost(ResourceType.mana, 8),
    spawns = "effect/Fireball",
  ),
)
