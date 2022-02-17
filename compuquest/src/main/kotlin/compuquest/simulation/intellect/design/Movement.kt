package compuquest.simulation.intellect.design

import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.general.World
import compuquest.simulation.general.interactionMaxDistance
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.navigation.NavigationState
import compuquest.simulation.intellect.navigation.fromRecastVector3
import compuquest.simulation.intellect.navigation.getNavigationPath
import compuquest.simulation.intellect.navigation.getNearestPoint
import godot.core.Vector3
import org.recast4j.detour.QueryFilter
import org.recast4j.detour.Status
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
		// First check if the actor is a pet that should be roaming close to its master.
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
	return if (spirit.personality.roaming) {
		val deck = world.deck
		val body = deck.bodies[actor]
		if (body != null) {
			val location = body.globalTransform.origin
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
