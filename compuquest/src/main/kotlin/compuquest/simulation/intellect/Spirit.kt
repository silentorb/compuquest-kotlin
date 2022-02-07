package compuquest.simulation.intellect

import compuquest.simulation.general.World
import compuquest.simulation.intellect.design.Goal
import compuquest.simulation.intellect.design.Goals
import compuquest.simulation.intellect.design.updateGoals
import compuquest.simulation.intellect.knowledge.Knowledge
import compuquest.simulation.intellect.knowledge.updateKnowledge
import godot.core.Vector3
import silentorb.mythic.ent.Id

const val spiritUpdateInterval = 20

data class Spirit(
	val intervalOffset: Int,
	val visibilityRange: Float = 20f,
	val knowledge: Knowledge = Knowledge(),
	val goal: Goal = Goal(),
)

private var newSpiritIntervalStep: Int = 0

fun nextNewSpiritInterval(): Int {
	val value = newSpiritIntervalStep
	newSpiritIntervalStep = if (value >= spiritUpdateInterval - 1)
		0
	else
		value + 1

	return value
}

fun newSpirit(): Spirit =
	Spirit(
		intervalOffset = nextNewSpiritInterval(),
	)

fun getSpiritIntervalStep(step: Long): Int =
	(step % spiritUpdateInterval.toLong()).toInt()

fun updateSpirit(world: World, intervalStep: Int): (Id, Spirit) -> Spirit = { actor, spirit ->
	val deck = world.deck
	if (intervalStep == spirit.intervalOffset && deck.characters[actor]?.isAlive == true) {
		val knowledge = updateKnowledge(world, actor, spirit)
		val goal = updateGoals(world, actor, spirit, knowledge)
		spirit.copy(
			knowledge = knowledge,
			goal = goal,
		)
	} else if (spirit.goal.readyToUseAction)
		spirit.copy(
			goal = spirit.goal.copy(
				readyToUseAction = false
			)
		)
	else
		spirit
}
