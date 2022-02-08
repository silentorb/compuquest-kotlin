package compuquest.simulation.intellect.design

import compuquest.simulation.characters.Character
import compuquest.simulation.general.*
import compuquest.simulation.intellect.knowledge.Knowledge
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.knowledge.getTargetRange
import compuquest.simulation.physics.Body
import silentorb.mythic.ent.Id

fun getVisibleTarget(world: World, goal: Goal, knowledge: Knowledge, actor: Id): Id? {
	val lastTarget = if (goal.targetEntity != null && world.dice.getInt(100) > 5)
		goal.targetEntity
	else
		null

	return getNextTarget(world, knowledge.visibleEnemies, actor, lastTarget)
}

fun getPathDestinations(goal: Goal, body: Body?) =
	if (goal.pathDestinations.any() && body != null &&
		body.translation.distanceTo(goal.pathDestinations.first()) < 1f
	)
		goal.pathDestinations.drop(1)
	else
		goal.pathDestinations

fun updateUseActionGoal(goal: Goal, accessory: Id): Goal {
	return goal.copy(
		focusedAction = accessory,
		readyToUseAction = true,
		destination = null,
	)
}

fun updateTargetPursuit(world: World, actor: Id, spirit: Spirit, knowledge: Knowledge): Goal {
	val deck = world.deck
	val goal = spirit.goal
	val visibleTarget = getVisibleTarget(world, goal, knowledge, actor)
	val body = deck.bodies[actor]
	val targetRange = if (body != null && visibleTarget != null)
		getTargetRange(world, body, visibleTarget)
	else
		null

	val accessory = if (targetRange != null)
		updateFocusedAction(world, actor)
	else
		null

	val isInRange = targetRange != null && accessory != null && targetRange <= accessory.value.definition.range

	val pathDestinations = getPathDestinations(goal, body)
	val lastKnownTargetLocation = knowledge.entityLocations[goal.targetEntity]

	val destination = when {
		visibleTarget != null && targetRange != null && accessory != null && !isInRange ->
			world.deck.bodies[visibleTarget]?.translation
		visibleTarget == null && lastKnownTargetLocation != null -> lastKnownTargetLocation
		isInRange -> null
		pathDestinations.any() && visibleTarget == null -> pathDestinations.first()
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
		destination = destination,
		readyToUseAction = destination == null && isInRange,
		pathDestinations = pathDestinations,
	)
}

fun checkSelfHealing(deck: Deck, actor: Id, character: Character): Id? {
	val health = character.health
	val maxHealth = character.destructible.maxHealth
	val gap = maxHealth - health
	val mostEfficient = getMostEfficientHealingAccessory(deck, actor, gap)
	return if (mostEfficient != null) {
		val healAmount = getAccessoryHealAmount(mostEfficient.value)
		val excess = health + healAmount - maxHealth
		if (excess < maxHealth + (maxHealth / 10) || health < maxHealth / 3)
			mostEfficient.key
		else
			null
	} else
		null
}

fun updateGoals(world: World, actor: Id, spirit: Spirit, knowledge: Knowledge): Goal {
	val goal = spirit.goal
	val deck = world.deck
	val character = deck.characters[actor]
	return when {
		character == null -> goal
		// This is split into a broad pass and a narrow pass filter for performance
		character.health <= character.destructible.maxHealth * 2 / 3 -> {
			val healingItem = checkSelfHealing(deck, actor, character)
			if (healingItem != null)
				updateUseActionGoal(spirit.goal, healingItem)
			else
				updateTargetPursuit(world, actor, spirit, knowledge)
		}
		else -> updateTargetPursuit(world, actor, spirit, knowledge)
	}
}
