package compuquest.definition

import compuquest.simulation.general.AccessoryAttributes
import compuquest.simulation.general.AccessoryDefinition
import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.ActionEffect
import silentorb.mythic.ent.KeyTable

object Accessories {
  val rifle = "rifle"
  val rocketLauncher = "rocketLauncher"
}

fun accessoryDefinitions(): KeyTable<AccessoryDefinition> = mapOf(
  Accessories.rifle to AccessoryDefinition(
    name = Accessories.rifle,
    cooldown = 0.2f,
    attributes = setOf(AccessoryAttributes.weapon),
    range = 30f,
    effects = listOf(
      ActionEffect(
        type = AccessoryEffects.attack,
        strength = 10f,
        spawns = "res://entities/effect/Fireball.tscn",
        speed = 75f,
      ),
    )
  ),
  Accessories.rocketLauncher to AccessoryDefinition(
    name = Accessories.rocketLauncher,
    cooldown = 1f,
    attributes = setOf(AccessoryAttributes.weapon),
    range = 5f,
    effects = listOf(
      ActionEffect(
        type = AccessoryEffects.attack,
        strength = 10f,
        spawns = "res://entities/effect/Fireball.tscn",
        speed = 30f,
      ),
    )
  ),
)
