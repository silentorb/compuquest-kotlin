package compuquest.simulation.combat

import compuquest.simulation.general.*
import compuquest.simulation.happening.useActionEvent
import compuquest.simulation.intellect.execution.tryUseAction
import compuquest.simulation.updating.simulationFps
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.hasEvent
import silentorb.mythic.happening.newEvent

fun isIntervalStep(step: Long, interval: Int): Boolean {
	val a = step / interval
	val b = a * interval
	return step - b == 0L
}

fun eventsFromPassiveBuffEffect(events: Events, accessory: Id, owner: Id, effect: AccessoryEffect): Events =
	when (effect.type) {
		AccessoryEffects.damage ->
			if (effect.recipient == EffectRecipient.self)
				listOf(newDamage(owner, effect.strengthInt))
			else
				listOf()

		AccessoryEffects.removeOnUseAny -> if (hasEvent(events, useActionEvent, owner))
			listOf(newEvent(deleteEntityCommand, accessory))
		else
			listOf()

		else -> listOf()
	}

fun eventsFromBuffs(deck: Deck, events: Events, filter: (AccessoryEffect) -> Boolean): Events =
	deck.accessories.flatMap { (id, accessory) ->
		accessory.definition.passiveEffects
			.filter(filter)
			.flatMap { effect ->
				eventsFromPassiveBuffEffect(events, id, accessory.owner, effect)
			}
	}

fun eventsFromBuffs(world: World): (Events) -> Events = { events ->
	if (isIntervalStep(world.step, simulationFps)) {
		eventsFromBuffs(world.deck, events) { it.interval > 0 }
	} else
		eventsFromBuffs(world.deck, events) { it.interval == 1 }
}
