package compuquest.simulation.happening

import compuquest.simulation.characters.*
import compuquest.simulation.combat.*
import compuquest.simulation.general.World
import compuquest.simulation.general.deleteEntityCommand
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

data class UseAction(
	val action: Id,
	val deferredEvents: Map<String, Event> = mapOf()
)

const val tryActionEvent = "tryAction"
const val useActionEvent = "useAction"

fun applyAccessoryEffect(
	world: World,
	actor: Id,
	targetEntity: Id,
	accessory: Accessory,
	effect: AccessoryEffect
): Events {
	val deck = world.deck
	val mod = getProficienceyModifier(deck, effect, actor)

	return when (effect.type) {
		AccessoryEffects.heal -> when (effect.recipient) {
			EffectRecipient.self -> listOf(modifyHealth(actor, effect.getStrength(mod)))
			EffectRecipient.raycast -> if (targetEntity != 0L)
				useHealingSpell(world, targetEntity, effect, mod)
			else
				raycastHeal(world, actor, accessory, effect)
			else -> listOf()
		}
		AccessoryEffects.summon -> summonInFrontOfActor(world, actor, accessory)
		AccessoryEffects.buff ->
			listOf(
				newAccessoryForContainer(
					actor, newAccessory(
						world.definitions,
						effect.buff,
						duration = effect.getDuration(mod)
					)
				)
			)

		AccessoryEffects.equipPrevious -> listOf(newEvent(equipPrevious, actor))
		else -> listOf()
	}
}

fun eventsFromTryAction(world: World): (Id, TryActionEvent) -> Events = { actor, event ->
	val deck = world.deck
	val action = event.action
	val targetEntity = event.targetEntity
	val accessory = getOwnerAccessory(deck, actor, action)
	if (accessory != null && canUse(accessory)) {
		val definition = accessory.definition
		val specificEvents =
			when {
				definition.isAttack -> listOf(
					startAttack(action, accessory, actor, event.targetLocation, targetEntity)
				)
				else -> listOf()
			}


		val effectEvents = accessory.definition.actionEffects
			.flatMap { effect ->
				applyAccessoryEffect(world, actor, targetEntity, accessory, effect)
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

		// Remove any effects that are configured to be removed when the actor uses an accessory
		val container = deck.containers[actor]!!
		val removeEffectEvents = container.accessories
			.mapNotNull { (id, accessory) ->
				if (accessory.definition.passiveEffects.any { it.type == AccessoryEffects.removeOnUseAny })
					newEvent(deleteEntityCommand, id)
				else
					null
			}

		val events = specificEvents + effectEvents + removeEffectEvents
		if (events.any())
			events + Event(
				type = useActionEvent,
				target = actor,
				value = UseAction(
					action = action,
					deferredEvents = mapOf()
				)
			) //+ paymentEvents
		else
			listOf() // Ability failed to perform any effects--consider it unused
	} else
		listOf()
}
