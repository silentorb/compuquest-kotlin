package compuquest.simulation.intellect.execution

import compuquest.simulation.general.InteractionBehaviors
import compuquest.simulation.general.Interactive
import compuquest.simulation.general.World
import compuquest.simulation.general.transferAccessory
import compuquest.simulation.intellect.design.Goal
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

fun tryInteraction(world: World, actor: Id, goal: Goal): Events {
	val interactive = world.bodies[goal.targetEntity] as? Interactive
	return if (interactive != null) {
		interactive.onInteraction(world, actor)
		listOf()
	} else when (goal.interactionBehavior) {
		InteractionBehaviors.give -> listOf(transferAccessory(goal.focusedAction!!, goal.targetEntity!!))
		else -> listOf()
	}
}
