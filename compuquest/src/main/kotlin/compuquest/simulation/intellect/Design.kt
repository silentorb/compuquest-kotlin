package compuquest.simulation.intellect

import compuquest.simulation.characters.Character
import compuquest.simulation.characters.getReadyAccessories
import compuquest.simulation.general.*
import godot.PhysicsDirectSpaceState
import godot.core.Vector3
import godot.core.variantArrayOf
import silentorb.mythic.ent.Id

fun filterEnemyTargets(
	world: World,
	actor: Id,
	character: Character
): Map<Id, Character> {
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
					&& isEnemy(world.factionRelationships, other.faction, character.faction)
					&& isVisible(world, godotBody, headLocation, id)
		}
}

fun getNextTarget(
	world: World,
	actor: Id,
	target: Id?
): Id? {
	val character = world.deck.characters[actor]!!
	val options = filterEnemyTargets(world, actor, character)
	return if (options.containsKey(target))
		target
	else {
		if (options.entries.size < 2)
			options.keys.firstOrNull()
		else {
			val deck = world.deck
			val bodies = deck.bodies
			val body = bodies[actor]!!
			options.keys
				.map { it to getTargetRange(world, body, it) }
				.minByOrNull { it.second }!!.first
		}
	}
}

fun updateDestination(world: World, actor: Id, targetLocation: Vector3?): Vector3? {
	val navigation = world.navigation
	val sourceLocation = world.deck.bodies[actor]?.translation
	if (navigation == null) {
		println("Current scene is not configured for pathfinding!")
	}
	return if (navigation != null && sourceLocation != null && targetLocation != null) {
//		val start = navigation.getClosestPoint(sourceLocation)
		val path = navigation.getSimplePath(sourceLocation, targetLocation)
		path.drop(1).firstOrNull { it.distanceTo(sourceLocation) > 0.1f }
	} else
		null
}

fun updateFocusedAction(world: World, actor: Id): Map.Entry<Id, Accessory>? {
	val readyActions = getReadyAccessories(world, actor)
	return readyActions.maxByOrNull { it.value.definition.range }
//world.dice.takeOneOrNull(readyActions.entries)
}

fun updateSpirit(world: World): (Id, Spirit) -> Spirit = { actor, spirit ->
	val target = getNextTarget(world, actor, spirit.target)
	val body = world.deck.bodies[actor]
	val targetRange = if (body != null && target != null)
		getTargetRange(world, body, target)
	else
		null

	val accessory = if (targetRange != null)
		updateFocusedAction(world, actor)
	else
		null

	val targetBody = if (target != null)
		world.deck.bodies[target]
	else
		null

	val lastKnownTargetLocation = when {
		targetBody != null -> targetBody.translation
		spirit.lastKnownTargetLocation != null && body != null &&
				spirit.lastKnownTargetLocation.distanceTo(body.translation) < 0.5f -> null
		else -> spirit.lastKnownTargetLocation
	}

	val isInRange = targetRange != null && accessory != null && targetRange <= accessory.value.definition.range

	val destination = when {
		target != null && targetRange != null && accessory != null && !isInRange ->
			updateDestination(world, actor, world.deck.bodies[target]?.translation)
		target == null && lastKnownTargetLocation != null -> updateDestination(world, actor, lastKnownTargetLocation)
		else -> null
	}

	spirit.copy(
		focusedAction = accessory?.key,
		target = target,
		nextDestination = destination,
		lastKnownTargetLocation = lastKnownTargetLocation,
		readyToUseAction = destination == null && isInRange,
	)
}
