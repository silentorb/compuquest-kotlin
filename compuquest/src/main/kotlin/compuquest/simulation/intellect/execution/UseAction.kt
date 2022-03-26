package compuquest.simulation.intellect.execution

import compuquest.simulation.characters.AccessoryEffects
import compuquest.simulation.characters.getOwnerAccessories
import compuquest.simulation.characters.getOwnerAccessory
import compuquest.simulation.general.World
import compuquest.simulation.happening.TryActionEvent
import compuquest.simulation.happening.tryActionEvent
import compuquest.simulation.intellect.design.Goal
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

fun tryUseAction(world: World, actor: Id, goal: Goal): Events {
	val deck = world.deck
	val action = goal.focusedAction
	val character = deck.characters[actor]

	return if (action != emptyId && character?.isAlive == true) {
		lookAtTarget(world, actor, goal)
		val accessory = getOwnerAccessory(world.deck, actor, action)
		val effects = accessory?.definition?.actionEffects ?: listOf()

		effects.flatMap { effect ->
			when (effect.type) {
				AccessoryEffects.summonAtTarget -> {
					val target = goal.targetEntity
					if (target != emptyId) {
						val body = deck.bodies[target]!!
						val targetLocation = body.globalTransform.origin //+ offset
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
		}
	} else
		listOf()
}
