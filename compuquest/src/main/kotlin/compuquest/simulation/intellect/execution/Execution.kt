package compuquest.simulation.intellect.execution

import compuquest.simulation.general.World
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

fun pursueGoals(world: World, actor: Id): Events {
	val deck = world.deck
	val character = deck.characters[actor]!!
	val spirit = world.deck.spirits[actor]
	return if (character.isAlive && spirit != null) {
		val goal = spirit.goal
		when {
			goal.destination != null -> moveTowardDestination(world, actor)
			goal.readyToUseAction -> tryUseAction(world, actor, spirit)
			else -> listOf()
		}
	} else
		listOf()
}
