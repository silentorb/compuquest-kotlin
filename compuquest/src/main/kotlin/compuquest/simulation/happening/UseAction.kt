package compuquest.simulation.happening

import compuquest.simulation.characters.canUse
import compuquest.simulation.combat.modifyHealth
import compuquest.simulation.combat.startAttack
import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.EffectRecipient
import compuquest.simulation.general.World
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

data class UseAction(
	val action: Id,
	val deferredEvents: Map<String, Event> = mapOf()
)

const val tryActionEvent = "tryAction"
const val useActionEvent = "useAction"

fun eventsFromTryAction(world: World): (Id, TryActionEvent) -> Events = { actor, event ->
	val deck = world.deck
	val action = event.action
	val targetEntity = event.targetEntity
	val accessory = deck.accessories[action]
	if (accessory != null && canUse(accessory)) {
		val definition = accessory.definition
		val specificEvents =
			when {
				definition.isAttack -> listOf(
					startAttack(action, accessory, actor, event.targetLocation, targetEntity)
				)

//      else -> when (definition.effect) {
//        Actions.dash -> dashEvents(definitions, accessory, actor)
//        Actions.entangle -> withResolvedTarget(world, actor, targetEntity, entangleEvents(deck, definition?.level ?: 1))
				else -> listOf()
//      }
			}

		val effectEvents = accessory.definition.actionEffects
			.flatMap { effect ->
				when (effect.type) {
					AccessoryEffects.heal -> if (effect.recipient == EffectRecipient.self)
						listOf(modifyHealth(actor, effect.strengthInt))
					else
						listOf()
					else -> listOf()
				}
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

		specificEvents + effectEvents + Event(
			type = useActionEvent,
			target = actor,
			value = UseAction(
				action = action,
				deferredEvents = mapOf()
			)
		) //+ paymentEvents
	} else
		listOf()
}

//fun eventsFromTryAction(world: World, freedomTable: FreedomTable): (TryActionEvent) -> Events = { event ->
//  val action = event.action
//  if (hasFreedom(freedomTable, event.actor, Freedom.acting) && canUse(world, action))
//    eventsFromTryAction(world, event)
//  else
//    listOf()
//}
