package compuquest.definition

import compuquest.simulation.general.AccessoryAttributes
import compuquest.simulation.general.AccessoryDefinition
import silentorb.mythic.ent.KeyTable

object Accessories {
  val rocketLauncher = "rocketLauncher"
}

fun accessoryDefinitions(): KeyTable<AccessoryDefinition> = mapOf(
  Accessories.rocketLauncher to AccessoryDefinition(
    name = Accessories.rocketLauncher,
    attributes = setOf(AccessoryAttributes.weapon),
  )
)
