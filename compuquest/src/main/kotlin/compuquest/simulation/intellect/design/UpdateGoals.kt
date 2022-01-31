package compuquest.simulation.intellect.design

import compuquest.simulation.general.*
import compuquest.simulation.intellect.knowledge.Knowledge
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.knowledge.getTargetRange
import silentorb.mythic.ent.Id

fun updateGoals(world: World, actor: Id, spirit: Spirit, knowledge: Knowledge): Goal {
	val deck = world.deck
	val goal = spirit.goal
	val lastTarget = if (goal.targetEntity != null && world.dice.getInt(100) > 5)
		goal.targetEntity
	else
		null

	val visibleTarget = getNextTarget(world, knowledge.visibleEnemies, actor, lastTarget)
	val body = deck.bodies[actor]
	val targetRange = if (body != null && visibleTarget != null)
		getTargetRange(world, body, visibleTarget)
	else
		null

	val accessory = if (targetRange != null)
		updateFocusedAction(world, actor)
	else
		null

//	val targetBody = if (visibleTarget != null)
//		deck.bodies[visibleTarget]
//	else
//		null
//
//	val targetJustDied = spirit.target != null && deck.characters[spirit.target]?.isAlive != true
//
//	val lastKnownTargetLocation = when {
//		targetJustDied -> null
//		targetBody != null -> targetBody.translation
//		spirit.lastKnownTargetLocation != null && body != null &&
//				spirit.lastKnownTargetLocation.distanceTo(body.translation) < 0.5f -> null
//		else -> spirit.lastKnownTargetLocation
//	}

	val isInRange = targetRange != null && accessory != null && targetRange <= accessory.value.definition.range

	val lastKnownTargetLocation = knowledge.entityLocations[goal.targetEntity]
	val pathDestinations = if (goal.pathDestinations.any() && body != null &&
		body.translation.distanceTo(goal.pathDestinations.first()) < 1f
	)
		goal.pathDestinations.drop(1)
	else
		goal.pathDestinations

	val destination = when {
		visibleTarget != null && targetRange != null && accessory != null && !isInRange ->
			updateDestination(world, actor, world.deck.bodies[visibleTarget]?.translation)
		visibleTarget == null && lastKnownTargetLocation != null -> updateDestination(
			world,
			actor,
			lastKnownTargetLocation
		)
		isInRange -> null
		pathDestinations.any() -> updateDestination(world, actor, pathDestinations.first())
		else -> null
	}

	val nextTarget = visibleTarget
		?: if (goal.targetEntity != null && deck.characters[goal.targetEntity]?.isAlive != true)
			null
		else
			goal.targetEntity

	return goal.copy(
		focusedAction = accessory?.key,
		targetEntity = nextTarget,
		immediateDestination = destination,
		readyToUseAction = destination == null && isInRange,
		pathDestinations = pathDestinations,
	)
}
