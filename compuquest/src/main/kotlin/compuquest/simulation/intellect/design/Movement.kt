package compuquest.simulation.intellect.design

import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.general.World
import compuquest.simulation.general.interactionMaxDistance
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.knowledge.Roaming
import compuquest.simulation.intellect.navigation.NavigationState
import compuquest.simulation.intellect.navigation.fromRecastVector3
import compuquest.simulation.intellect.navigation.getNavigationPath
import compuquest.simulation.intellect.navigation.getNearestPoint
import godot.core.Vector3
import org.recast4j.detour.QueryFilter
import org.recast4j.detour.Status
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id

fun moveWithinRange(
	navigation: NavigationState,
	location: Vector3,
	destination: Vector3,
	range: Float,
	goal: Goal,
	onInRange: () -> Goal?
): Goal? {
	val distance = location.distanceTo(destination)
	return if (distance > range) {
		val path = getNavigationPath(navigation, location, destination, range)
		if (path.any())
			goal.copy(
				readyTo = ReadyMode.move,
				destination = destination,
			)
		else
			null
	} else
		onInRange()
}

fun moveWithinRange(
	world: World,
	actor: Id,
	destination: Vector3,
	range: Float,
	goal: Goal,
	onInRange: () -> Goal?
): Goal? {
	val deck = world.deck
	val body = deck.bodies[actor] as? CharacterBody
	val navigation = world.navigation
	return if (body != null && navigation != null) {
		moveWithinRange(navigation, body.location, destination, range, goal, onInRange)
	} else
		null
}

fun moveWithinRange(
	world: World,
	actor: Id,
	target: Id,
	range: Float,
	goal: Goal,
	onInRange: () -> Goal?
): Goal? {
	val deck = world.deck
	val otherBody = deck.bodies[target] as? CharacterBody
	return if (otherBody != null) {
		moveWithinRange(world, actor, otherBody.location, range, goal, onInRange)
	} else
		null
}

fun getPointIfReachable(navigation: NavigationState, filter: QueryFilter, location: Vector3, point: Vector3): Vector3? {
	val pathResult = getNavigationPath(navigation, location, point, filter)
	return if (pathResult.status == Status.SUCCSESS)
		point
	else
		null
}

fun getRandomNavMeshPoint(navigation: NavigationState, filter: QueryFilter, location: Vector3): Vector3? {
	val pointResult = navigation.query.findRandomPoint(filter, navigation.rand)
	return if (pointResult.succeeded()) {
		val point = fromRecastVector3(pointResult.result.randomPt)
		getPointIfReachable(navigation, filter, location, point)
	} else
		null
}

fun getAnchoredRoaming(world: World, filter: QueryFilter, actor: Id, location: Vector3): Vector3? {
	val deck = world.deck
	val navigation = world.navigation!!
	val character = deck.characters[actor]
	val master = character?.relationships
		?.firstOrNull { it.isA == RelationshipType.master }
		?.of

	val masterLocation = deck.bodies[master]?.globalTransform?.origin
	return if (masterLocation != null) {
		val dice = world.dice
		val distance = dice.getFloat(2f, 10f)
		val offset = Vector3(dice.getFloat(-1f, 1f), 0f, dice.getFloat(-1f, 1f)).normalized() * distance
		val roughPoint = masterLocation + offset
		val point = getNearestPoint(navigation, roughPoint)
		if (point != null)
			getPointIfReachable(navigation, filter, location, point)
		else
			null
	} else
		null
}

fun newRoamingDestination(world: World, actor: Id, location: Vector3): Vector3? {
	val navigation = world.navigation!!
	val agent = world.navigation.agents[actor]
	return if (agent != null) {
		val filter = navigation.crowd.getFilter(agent.params.queryFilterType)
		// First check if the actor is a pet that should be roaming near its master.
		// If not, next try twice to increase the likelihood of getting a valid point.
		// If two tries don't work then this code will just be hit again the next time the spirit updates its goals
		getAnchoredRoaming(world, filter, actor, location)
			?: getRandomNavMeshPoint(navigation, filter, location)
			?: getRandomNavMeshPoint(navigation, filter, location)
	} else
		null
}

fun checkRoaming(world: World, actor: Id, spirit: Spirit): Goal {
	val goal = spirit.goal
	val roaming = spirit.personality.roaming
	if (goal.destination != null && goal.readyTo == ReadyMode.none)
		return goal.copy(destination = null)

	return if (roaming == Roaming.roaming || (roaming == Roaming.roamWhenAlerted && goal.isAlerted)) {
		val deck = world.deck
		val body = deck.bodies[actor] as? CharacterBody
		if (body != null) {
			val location = body.location
			val previousDestination = goal.destination
			if (previousDestination == null || location.distanceTo(previousDestination) < interactionMaxDistance * 1.5f) {
				val dice = world.dice
				checkGoalPause(goal)
					?: if (dice.getInt(100) < 70)
						goal.copy(
							readyTo = ReadyMode.move,
							destination = newRoamingDestination(world, actor, location),
						)
					else
						goal.copy(
							readyTo = ReadyMode.none,
							pause = 20,
						)
			} else
				goal
		} else
			goal
	} else
		goal
}

fun checkPathDestinations(world: World, actor: Id, spirit: Spirit): Goal? {
	val deck = world.deck
	val goal = spirit.goal
	val body = deck.bodies[actor]
	val pathDestinations = getPathDestinations(goal, body)
	return if (pathDestinations.any())
		goal.copy(
			readyTo = ReadyMode.move,
			destination = pathDestinations.first(),
		)
	else
		null
}
