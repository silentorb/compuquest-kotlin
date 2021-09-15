package compuquest.simulation.general

import compuquest.simulation.definition.AccessoryDefinition
import compuquest.simulation.definition.Cost
import compuquest.simulation.definition.Definitions
import compuquest.simulation.happening.Events
import compuquest.simulation.happening.filterEventValues
import silentorb.mythic.ent.Id

data class Accessory(
  val owner: Id,
  val level: Int? = null,
  val cooldown: Float = 0f,
  val maxCooldown: Float = 0f,
  val name: String,
  val range: Float = 0f,
  val cost: Cost? = null,
  val spawns: Key? = null,
)

const val useActionCommand = "useAction"

fun updateAccessory(definitions: Definitions, events: Events): (Id, Accessory) -> Accessory {
  val uses = filterEventValues<Id>(useActionCommand, events)
  return { id, accessory ->
    val used = uses.contains(id)
    if (used) {
      accessory.copy(
        cooldown = accessory.maxCooldown,
      )
    } else
      accessory
  }
}

fun accessoryFromDefinition(definition: AccessoryDefinition, owner: Id) =
  Accessory(
    owner = owner,
    maxCooldown = definition.cooldown,
    name = definition.name,
    range = definition.range,
    cost = definition.cost,
    spawns = definition.spawns,
  )
