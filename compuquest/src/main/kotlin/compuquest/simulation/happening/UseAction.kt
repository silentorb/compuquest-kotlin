package compuquest.simulation.happening

import compuquest.simulation.combat.startAttack
import compuquest.simulation.general.AccessoryAttributes
import compuquest.simulation.general.World
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.GenericEvent

data class UseAction(
  val action: Id,
  val deferredEvents: Map<String, Event> = mapOf()
)

const val tryActionEvent = "tryAction"

const val useActionEvent = "useAction"

fun eventsFromTryAction(world: World): (Id, TryActionEvent) -> Events = { actor, event ->
  val definitions = world.definitions
  val deck = world.deck
  val action = event.action
  val targetEntity = event.targetEntity
  val accessory = deck.accessories[action]!!
  val definition = accessory.definition
  val isWeapon = definition.hasAttribute(AccessoryAttributes.weapon)
  val specificEvents =
    when {
      isWeapon -> listOf(
        startAttack(action, accessory, actor, event.targetLocation, event.targetEntity)
      )
//      else -> when (definition.effect) {
//        Actions.dash -> dashEvents(definitions, accessory, actor)
//        Actions.entangle -> withResolvedTarget(world, actor, targetEntity, entangleEvents(deck, definition?.level ?: 1))
      else -> listOf()
//      }
    }
  val cost = definition.cost
//  val paymentEvents = if (cost.isNotEmpty())
//    listOf(
//      ModifyResource(
//        actor = actor,
//        resource = cost.type,
//        amount = -cost.amount,
//      )
//    )
//  else
//    listOf()

  specificEvents + Event(
    type = useActionEvent,
    target = actor,
    value = UseAction(
      action = action,
      deferredEvents = mapOf()
    )
  ) //+ paymentEvents
}

//fun eventsFromTryAction(world: World, freedomTable: FreedomTable): (TryActionEvent) -> Events = { event ->
//  val action = event.action
//  if (hasFreedom(freedomTable, event.actor, Freedom.acting) && canUse(world, action))
//    eventsFromTryAction(world, event)
//  else
//    listOf()
//}