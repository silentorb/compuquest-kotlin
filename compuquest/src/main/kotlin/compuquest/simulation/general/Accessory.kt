package compuquest.simulation.general

import compuquest.simulation.definition.TypedResource
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventTargets
import kotlin.math.max

data class Accessory(
  val owner: Id,
  val level: Int? = null,
  val cooldown: Float = 0f,
  val maxCooldown: Float = 0f,
  val name: String,
  val range: Float = 0f,
  val cost: ResourceMap = mapOf(),
  val spawns: Key? = null,
  val attributes: Set<Key>,
  val effect: Key,
  val strength: Float = 0f,
) {
  val strengthInt: Int get() = strength.toInt()
}

object AccessoryEffects {
  val attack = "attack"
  val heal = "heal"
}

const val useActionCommand = "useAction"
const val detrimentalEffectCommand = "detrementalEffect"

fun updateAccessory(events: Events, delta: Float): (Id, Accessory) -> Accessory {
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

//fun accessoryFromDefinition(definition: AccessoryDefinition, owner: Id) =
//  Accessory(
//    owner = owner,
//    maxCooldown = definition.cooldown,
//    name = definition.name,
//    range = definition.range,
//    cost = definition.cost,
//    spawns = definition.spawns,
//  )
