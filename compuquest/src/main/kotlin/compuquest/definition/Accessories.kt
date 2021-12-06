package compuquest.definition

import compuquest.simulation.general.AccessoryAttributes
import compuquest.simulation.general.AccessoryDefinition
import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.ActionEffect
import silentorb.mythic.ent.KeyTable

object Accessories {
  val rocketLauncher = "rocketLauncher"
}

fun accessoryDefinitions(): KeyTable<AccessoryDefinition> = mapOf(
  Accessories.rocketLauncher to AccessoryDefinition(
    name = Accessories.rocketLauncher,
    cooldown = 0.2f,
    attributes = setOf(AccessoryAttributes.weapon),
    range = 20f,
    effects = listOf(
      ActionEffect(
        type = AccessoryEffects.attack,
        strength = 10f,
        spawns = "res://entities/effect/Fireball.tscn",
        speed = 10f,
      ),
    )
  )
)
