package compuquest.simulation.intellect.navigation

import compuquest.simulation.general.World
import godot.core.Vector3
import org.recast4j.detour.DefaultQueryFilter
import org.recast4j.detour.FindNearestPolyResult
import org.recast4j.detour.Status
import org.recast4j.detour.StraightPathItem
import org.recast4j.detour.crowd.CrowdAgent
import org.recast4j.detour.crowd.PathQueryResult
import silentorb.mythic.ent.Id

fun nearestPolygon(navigation: NavigationState, target: Vector3): org.recast4j.detour.Result<FindNearestPolyResult>? {
	val polygonRange = floatArrayOf(10f, 10f, 10f)
	val queryFilter = DefaultQueryFilter()
	val end = toRecastVector3(target)
	val nearest = navigation.query.findNearestPoly(end, polygonRange, queryFilter)
	return if (!nearest.failed())
		nearest
	else
		null
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
	val queryFilter = DefaultQueryFilter()
	val startPolygon = nearestPolygon(navigation, startPosition) ?: return listOf()

	val endPolygon = nearestPolygon(navigation, targetPosition) ?: return listOf()

	val path = query.findPath(
		startPolygon.result.nearestRef,
		endPolygon.result.nearestRef,
		start,
		end,
		queryFilter
	)

	if (path.failed())
		return listOf()

	val pathResult = query.findStraightPath(start, end, path.result, 2, 0)
	if (pathResult.failed())
		return listOf()

	val lastPoint = fromRecastVector3(pathResult.result.last().pos)
	return if (lastPoint.distanceTo(targetPosition) > successRange)
		listOf()
	else
		pathResult.result
}

fun getNavigationAgentVelocity(agent: CrowdAgent): Vector3 {
	val queryState = crowdAgent_targetPathQueryResult?.get(agent) as? PathQueryResult?
	val status = if (queryState != null)
		pathQueryResult_status?.get(queryState) as Status?
	else
		null

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
