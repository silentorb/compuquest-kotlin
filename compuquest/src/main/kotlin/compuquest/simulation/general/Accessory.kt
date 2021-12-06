package compuquest.simulation.general

import compuquest.simulation.definition.Definitions
import compuquest.simulation.happening.UseAction
import compuquest.simulation.happening.useActionEvent
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.NextId
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventTargets
import kotlin.math.max

data class ActionEffect(
  val type: String,
  val strength: Float = 0f,
  val spawns: Key? = null,
  val speed: Float = 1f
) {
  val strengthInt: Int get() = strength.toInt()
}

data class AccessoryDefinition(
  val cooldown: Float = 0f,
  val name: String,
  val range: Float = 0f,
  val cost: ResourceMap = mapOf(),
  val attributes: Set<Key> = setOf(),
  val effects: List<ActionEffect> = listOf(), // Currently only zero or one effects are fully supported.  Stored as an array to minimize future refactoring
  val animation: Key? = null,
) {
  fun hasAttribute(attribute: String): Boolean = attributes.contains(attribute)
}

data class Accessory(
  val owner: Id,
  val level: Int? = null,
  val cooldown: Float = 0f,
  val definition: AccessoryDefinition,
)

object AccessoryEffects {
  val attack = "attack"
  val heal = "heal"
  val resurrect = "resurrect"
  val damageReduction = "armor"
}

object AccessoryAttributes {
  const val weapon = "weapon"
}

const val detrimentalEffectCommand = "detrementalEffect"

fun newAccessory(definitions: Definitions, nextId: NextId, owner: Id, type: Key): Hand {
  val definition = definitions.accessories[type]
  if (definition == null)
    throw Error("Invalid accessory type $type")

  return Hand(
    id = nextId(),
    components = listOf(
      Accessory(
        owner = owner,
        definition = definition,
//        name = getString(accessory, "name"),
//        maxCooldown = getFloat(accessory, "cooldown"),
//        range = getFloat(accessory, "Range"),
//        cost = mapOf(
//          (getResourceType(accessory, "costResource") ?: ResourceType.mana) to
//              getInt(accessory, "costAmount")
//        ),
//        spawns = (accessory.get("spawns") as? Resource)?.resourcePath,
//        effect = getString(accessory, "effect"),
//        strength = getFloat(accessory, "strength"),
//        attributes = getList<Key>(accessory, "attributes").toSet()
      )
    )
  )
}

fun updateAccessory(events: Events, delta: Float): (Id, Accessory) -> Accessory {
  val uses = events
    .filter { it.type == useActionEvent }
    .mapNotNull { (it.value as? UseAction)?.action }
  return { id, accessory ->
    val used = uses.contains(id)
    val cooldown = if (used)
      accessory.definition.cooldown
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
