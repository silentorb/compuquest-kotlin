package compuquest.simulation.intellect

import compuquest.simulation.characters.Character
import compuquest.simulation.characters.getReadyAccessories
import compuquest.simulation.general.*
import godot.PhysicsDirectSpaceState
import godot.core.Vector3
import godot.core.variantArrayOf
import silentorb.mythic.ent.Id

const val spiritRangeBuffer = 0.1f

fun inRangeAndVisible(
	space: PhysicsDirectSpaceState,
	world: World,
	bodyId: Id,
	body: Body,
	other: Id,
	range: Float
): Boolean {
	val otherBody = world.bodies[other]
	val location = body.translation + Vector3(0f, 1f, 0f)

	return otherBody != null
			&& location.distanceTo(otherBody.translation + Vector3(0f, 1f, 0f)) <= range
			&& space.intersectRay(body.translation, otherBody.translation, variantArrayOf(world.bodies[bodyId]!!, otherBody))
		.none()
}

fun getTargetRange(
	world: World,
	body: Body,
	other: Id
): Float {
	val otherBody = world.bodies[other]!!
	val location = body.translation + Vector3(0f, 1f, 0f)
	return location.distanceTo(otherBody.translation + Vector3(0f, 1f, 0f)).toFloat()
}

fun isVisible(
	space: PhysicsDirectSpaceState,
	world: World,
	bodyId: Id,
	body: Body,
	other: Id
): Boolean {
	val otherBody = world.bodies[other]
	return otherBody != null &&
			space.intersectRay(body.translation, otherBody.translation, variantArrayOf(world.bodies[bodyId]!!, otherBody))
				.none()
}

fun filterEnemyTargets(
	world: World,
	actor: Id,
	character: Character
): Map<Id, Character> {
	val deck = world.deck
	val bodies = deck.bodies
	val body = bodies[actor] ?: return mapOf()
	val space = getSpace(world) ?: return mapOf()
	return deck.characters
		.filter { (id, other) ->
			id != actor
					&& other.isAlive
					&& isEnemy(world.factionRelationships, other.faction, character.faction)
					&& isVisible(space, world, actor, body, id)
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

	val destination = when {
		target != null && targetRange != null && accessory != null &&
				targetRange > accessory.value.definition.range ->
			updateDestination(world, actor, world.deck.bodies[target]?.translation)
		target == null && lastKnownTargetLocation != null -> updateDestination(world, actor, lastKnownTargetLocation)
		else -> null
	}

	spirit.copy(
		focusedAction = accessory?.key,
		target = target,
		nextDestination = destination,
		lastKnownTargetLocation = lastKnownTargetLocation
	)
}
