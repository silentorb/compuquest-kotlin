package compuquest.definition

import compuquest.simulation.definition.AccessoryDefinition
import compuquest.simulation.definition.Cost
import compuquest.simulation.definition.Resources
import compuquest.simulation.general.Key

fun defineAccessories(): Map<Key, AccessoryDefinition> = mapOf(
  "castFireball" to AccessoryDefinition(
    name = "Fireball",
    range = 15f,
    cooldown = 2f,
    usageCost = Cost(Resources.mana, 8),
    spawns = "fireball",
  ),
)
