package compuquest.simulation.intellect.execution

import compuquest.simulation.general.World
import compuquest.simulation.intellect.design.ReadyMode
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

fun pursueGoals(world: World, actor: Id): Events {
	val deck = world.deck
	val character = deck.characters[actor]!!
	val spirit = world.deck.spirits[actor]
	return if (character.isAlive && spirit != null) {
		val goal = spirit.goal
		when(goal.readyTo) {
			ReadyMode.move -> moveTowardDestination(world, actor)
			ReadyMode.action -> tryUseAction(world, actor, goal)
			ReadyMode.interact -> tryInteraction(world, actor, goal)
			else -> listOf()
		}
	} else
		listOf()
}
