package compuquest.simulation.combat

import compuquest.simulation.general.Accessory
import compuquest.simulation.general.AccessoryEffect
import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.World
import compuquest.simulation.updating.simulationFps
import silentorb.mythic.happening.Events

fun isIntervalStep(step: Long, interval: Int): Boolean {
	val a = step / interval
	val b = a * interval
	return step - b == 0L
}

fun eventsFromBuffEffect(accessory: Accessory, effect: AccessoryEffect): Events =
	when (effect.type) {
		AccessoryEffects.damageSelf -> listOf(newDamage(accessory.owner, effect.strengthInt))
		else -> listOf()
	}

fun eventsFromBuffs(world: World): Events {
	return if (isIntervalStep(world.step, simulationFps)) {
		val deck = world.deck
		deck.accessories.values.flatMap { accessory ->
			accessory.definition.passiveEffects
				.filter { it.interval != 0 }
				.flatMap { effect ->
					eventsFromBuffEffect(accessory, effect)
				}
		}
	} else
		listOf()
}
