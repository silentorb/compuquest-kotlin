package compuquest.simulation.intellect.design

import compuquest.simulation.characters.Character
import compuquest.simulation.general.World
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.knowledge.Knowledge
import compuquest.simulation.intellect.knowledge.getTargetRange
import compuquest.simulation.intellect.knowledge.isEnemy
import compuquest.simulation.intellect.knowledge.isVisible
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table

fun filterEnemyTargets(
	world: World,
	actor: Id,
	character: Character,
	visibilityRange: Float
): Table<Character> {
	val deck = world.deck
	val bodies = deck.bodies
	val body = bodies[actor] ?: return mapOf()
	val godotBody = world.bodies[actor] ?: return mapOf()
	// Add a random offset as a heuristic to deal with complex terrain and a lack of sphere casting.
	// Occasionally, a spirit will try to attack a character through a space that is wide enough
	// for a ray but not for the spirit's projectile size.
	// This heuristic will minimize how often a spirit continuously attempts such a futile feat.
	// It also mixes up visibility checks when the target would be barely visible
	val r = 0.4f
	val randomPadding = Vector3(world.dice.getFloat(-r, r), world.dice.getFloat(-r, r), world.dice.getFloat(-r, r))
	val headLocation = body.translation + character.toolOffset + randomPadding
	return deck.characters
		.filter { (id, other) ->
			id != actor
					&& other.isAlive
					&& isEnemy(world, actor, id)
					&& isVisible(world, godotBody, headLocation, id, visibilityRange)
		}
}

fun getNextTarget(
	world: World,
	visibleEnemies: Table<Character>,
	actor: Id,
	target: Id?
): Id? {
	return if (visibleEnemies.containsKey(target))
		target
	else {
		if (visibleEnemies.entries.size < 2)
			visibleEnemies.keys.firstOrNull()
		else {
			val deck = world.deck
			val bodies = deck.bodies
			val body = bodies[actor]!!
			visibleEnemies.keys
				.map { it to getTargetRange(world, body, it) }
				.minByOrNull { it.second }!!.first
		}
	}
}

fun getVisibleTarget(world: World, goal: Goal, knowledge: Knowledge, actor: Id): Id? {
	val lastTarget = if (goal.targetEntity != null && world.dice.getInt(100) > 5)
		goal.targetEntity
	else
		null

	return getNextTarget(world, knowledge.visibleEnemies, actor, lastTarget)
}

fun checkTargetPursuit(world: World, actor: Id, spirit: Spirit, knowledge: Knowledge): Goal {
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
		readyTo = when {
			destination != null -> ReadyMode.move
			isInRange -> ReadyMode.action
			else -> ReadyMode.none
		},
		pathDestinations = pathDestinations,
	)
}
