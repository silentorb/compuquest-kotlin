package compuquest.simulation.intellect.navigation

import compuquest.simulation.general.World
import godot.core.Vector3
import org.recast4j.detour.DefaultQueryFilter
import org.recast4j.detour.FindNearestPolyResult
import org.recast4j.detour.crowd.CrowdAgent
import silentorb.mythic.ent.Id

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

fun getNavigationAgentVelocity(agent: CrowdAgent): Vector3 {
  val velocity = fromRecastVector3(agent.vel)
  return if (velocity == Vector3.ZERO)
    Vector3.ZERO
  else
    velocity.normalized()
}

fun getNavigationAgentVelocity(world: World, actor: Id): Vector3 =
  getNavigationAgentVelocity(world.navigation!!.agents[actor]!!)
