package compuquest.simulation.general

import compuquest.simulation.definition.AccessoryDefinition
import compuquest.simulation.definition.Cost
import compuquest.simulation.definition.Definitions
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventTargets
import silentorb.mythic.ent.Id
import kotlin.math.max

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

fun updateAccessory(definitions: Definitions, events: Events, delta: Float): (Id, Accessory) -> Accessory {
  val uses = filterEventTargets<Id>(useActionCommand, events)
  return { id, accessory ->
    val used = uses.contains(id)
    val cooldown = if (used)
      accessory.maxCooldown
    else
      max(0f, accessory.cooldown - delta)

    accessory.copy(
      cooldown = cooldown
    )
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
