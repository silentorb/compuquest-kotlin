package compuquest.simulation.combat

import compuquest.simulation.characters.AccessoryEffect
import compuquest.simulation.characters.AccessoryEffects
import compuquest.simulation.characters.ContainerEffectsMode
import compuquest.simulation.characters.EffectRecipient
import compuquest.simulation.general.*
import compuquest.simulation.updating.simulationFps
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

fun isIntervalStep(step: Long, interval: Int): Boolean {
	val a = step / interval
	val b = a * interval
	return step - b == 0L
}

fun eventsFromPassiveBuffEffect(deck: Deck, actor: Id, effect: AccessoryEffect): Events =
	when (effect.type) {
		AccessoryEffects.damage ->
			if (effect.recipient == EffectRecipient.self)
				newDamageEvents(actor, newDamages(deck, actor, effect))
			else
				listOf()

		else -> listOf()
	}

fun eventsFromBuffs(deck: Deck, filter: (AccessoryEffect) -> Boolean): Events =
	deck.containers
		.filterValues { it.effectsMode == ContainerEffectsMode.usesEffects }
		.flatMap { (actor, container) ->
			container.accessories.flatMap { (_, accessory) ->
				accessory.definition.passiveEffects
					.filter(filter)
					.flatMap { effect ->
						eventsFromPassiveBuffEffect(deck, actor, effect)
					}
			}
		}

fun eventsFromBuffs(world: World): Events =
	if (isIntervalStep(world.step, simulationFps)) {
		eventsFromBuffs(world.deck) { it.interval != 0 }
	} else
		eventsFromBuffs(world.deck) { it.interval == 1 }
