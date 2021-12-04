package compuquest.simulation.happening

import compuquest.simulation.general.AccessoryAttributes
import compuquest.simulation.general.World
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

data class UseAction(
  val action: Id,
  val deferredEvents: Map<String, Event> = mapOf()
)

const val useActionEvent = "useAction"

//fun eventsFromTryAction(world: World, event: TryActionEvent): Events {
//  val definitions = world.definitions
//  val deck = world.deck
//  val actor = event.actor
//  val action = event.action
//  val targetEntity = event.targetEntity
//  val accessory = deck.accessories[action]!!
//  val type = accessory.type
//  val definition = definitions.accessories[type]!!
//  val isWeapon = definition.hasAttribute(AccessoryAttributes.weapon)
//  val specificEvents =
//    when {
//      isWeapon -> listOf(
//        startAttack(definitions.actions[type]!!, actor, action, type, event.targetLocation, event.targetEntity)
//      )
//      else -> when (definition.effect) {
//        Actions.dash -> dashEvents(definitions, accessory, actor)
//        Actions.entangle -> withResolvedTarget(world, actor, targetEntity, entangleEvents(deck, definition?.level ?: 1))
//        else -> listOf()
//      }
//    }
//  val cost = definition.cost
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
//
//  return specificEvents + Event(
//    type = useActionEvent,
//    target = actor,
//    value = UseAction(
//      action = action,
//      deferredEvents = mapOf()
//    )
//  ) + paymentEvents
//}

//fun eventsFromTryAction(world: World, freedomTable: FreedomTable): (TryActionEvent) -> Events = { event ->
//  val action = event.action
//  if (hasFreedom(freedomTable, event.actor, Freedom.acting) && canUse(world, action))
//    eventsFromTryAction(world, event)
//  else
//    listOf()
//}
