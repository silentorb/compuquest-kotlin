package compuquest.simulation.intellect.execution

import compuquest.simulation.general.Interactive
import compuquest.simulation.general.World
import compuquest.simulation.intellect.design.Goal
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

fun tryInteraction(world: World, actor: Id, goal: Goal): Events {
	val interactive = world.bodies[goal.targetEntity] as? Interactive
	return if (interactive != null) {
		interactive.onInteraction(world, actor)
		listOf()
	} else
		listOf()
}
