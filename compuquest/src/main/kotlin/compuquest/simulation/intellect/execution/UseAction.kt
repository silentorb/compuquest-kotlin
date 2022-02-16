package compuquest.simulation.intellect.execution

import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.World
import compuquest.simulation.happening.TryActionEvent
import compuquest.simulation.happening.tryActionEvent
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.design.Goal
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

fun tryUseAction(world: World, actor: Id, goal: Goal): Events {
	val action = goal.focusedAction
	val accessory = world.deck.accessories[action]
	val effects = accessory?.definition?.actionEffects ?: listOf()
	lookAtTarget(world, actor, goal)

	return if (action != null)
		effects.flatMap { effect ->
			when (effect.type) {
				AccessoryEffects.summonAtTarget -> {
					val target = goal.targetEntity
					if (target != null) {
						val body = world.deck.bodies[target]!!
						val targetLocation = body.translation //+ offset
						val value = TryActionEvent(
							action = action,
							targetLocation = targetLocation,
							targetEntity = target, // Not needed for the summoning but used to trigger user targeting effects
						)
						listOf(newEvent(tryActionEvent, actor, value))
					} else
						listOf()
				}
				else -> {
					listOf(newEvent(tryActionEvent, actor, TryActionEvent(action = action, targetEntity = goal.targetEntity)))
				}
			}
		} else
		listOf()
}
