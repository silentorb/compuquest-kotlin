package compuquest.simulation.intellect.navigation

import godot.core.Vector3
import org.recast4j.detour.DefaultQueryFilter
import org.recast4j.detour.FindNearestPolyResult

fun nearestPolygon(navigation: NavigationState, target: Vector3): org.recast4j.detour.Result<FindNearestPolyResult>? {
  val polygonRange = floatArrayOf(10f, 10f, 10f)
  val queryFilter = DefaultQueryFilter()
  val end = toRecastVector3(target)
  val nearest = navigation.query.findNearestPoly(end, polygonRange, queryFilter)
  return if (!nearest.failed() && nearest.result.nearestRef != 0L)
    nearest
  else
    null
}
