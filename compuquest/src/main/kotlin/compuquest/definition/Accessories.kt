package compuquest.definition

import compuquest.simulation.definition.AccessoryDefinition
import compuquest.simulation.definition.Cost
import compuquest.simulation.definition.Resources
import compuquest.simulation.general.Key

fun defineAccessories(): Map<Key, AccessoryDefinition> = mapOf(
  "fireball" to AccessoryDefinition(
    name = "Fireball",
    range = 15f,
    cooldown = 2f,
    cost = Cost(Resources.mana, 8),
    spawns = "effect/Fireball",
  ),
)
