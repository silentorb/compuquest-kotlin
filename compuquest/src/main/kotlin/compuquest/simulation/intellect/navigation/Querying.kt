package compuquest.simulation.intellect.navigation

import compuquest.simulation.general.World
import godot.core.Vector3
import org.recast4j.detour.*
import org.recast4j.detour.crowd.CrowdAgent
import silentorb.mythic.ent.Id

fun getNearestPolygon(navigation: NavigationState, target: Vector3): Result<FindNearestPolyResult>? {
	val polygonRange = floatArrayOf(10f, 10f, 10f)
	val queryFilter = DefaultQueryFilter()
	val end = toRecastVector3(target)
	val nearest = navigation.query.findNearestPoly(end, polygonRange, queryFilter)
	return if (!nearest.failed())
		nearest
	else
		null
}

fun getNearestPoint(navigation: NavigationState, target: Vector3): Vector3? {
	val polygon = getNearestPolygon(navigation, target)
	return if (polygon != null) {
		val pointResult = navigation.query.closestPointOnPoly(polygon.result.nearestRef, toRecastVector3(target))
		if (pointResult.succeeded())
			fromRecastVector3(pointResult.result.closest)
		else
			null
	} else
		null
}

fun getNavigationPath(
	navigation: NavigationState,
	startPosition: Vector3,
	targetPosition: Vector3,
	filter: QueryFilter = DefaultQueryFilter()
): Result<List<Long>> {
	val query = navigation.query

	val start = toRecastVector3(startPosition)
	val end = toRecastVector3(targetPosition)
	val startPolygon = getNearestPolygon(navigation, startPosition) ?: return Result.failure()

	val endPolygon = getNearestPolygon(navigation, targetPosition) ?: return Result.failure()

	return query.findPath(
		startPolygon.result.nearestRef,
		endPolygon.result.nearestRef,
		start,
		end,
		filter
	)
}

fun getNavigationPath(
	navigation: NavigationState,
	startPosition: Vector3,
	targetPosition: Vector3,
	successRange: Float
): List<StraightPathItem> {
	val query = navigation.query
	val start = toRecastVector3(startPosition)
	val end = toRecastVector3(targetPosition)
	val path = getNavigationPath(navigation, startPosition, targetPosition)

	if (path.failed())
		return listOf()

	val pathResult = query.findStraightPath(start, end, path.result, 2, 0)
	if (pathResult.failed())
		return listOf()

	return pathResult.result

//	val lastPoint = fromRecastVector3(pathResult.result.last().pos)
//	return if (lastPoint.distanceTo(targetPosition) > successRange)
//		listOf()
//	else
//		pathResult.result
}

fun getNavigationAgentVelocity(agent: CrowdAgent): Vector3 {
	val status = getAgentStatus(agent)

	if (status == Status.PARTIAL_RESULT)
		return Vector3.ZERO

	val velocity = fromRecastVector3(agent.vel)
	return if (velocity == Vector3.ZERO)
		Vector3.ZERO
	else
		velocity.normalized()
}

fun getNavigationAgentVelocity(world: World, actor: Id): Vector3? {
	val agent = world.navigation!!.agents[actor]
	return if (agent != null)
		getNavigationAgentVelocity(agent)
	else
		null
}
