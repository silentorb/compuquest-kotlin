package compuquest.simulation.intellect.navigation

import compuquest.simulation.general.Deck
import godot.core.Vector3
import org.recast4j.detour.crowd.CrowdAgent
import org.recast4j.detour.crowd.CrowdAgentParams
import org.recast4j.detour.crowd.debug.CrowdAgentDebugInfo
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table

fun newCrowdAgentParams(actor: Id, maxSpeed: Float): CrowdAgentParams {
	val params = CrowdAgentParams()

	params.radius = agentRadius
	params.height = agentHeight
	params.maxAcceleration = 8f
	params.maxSpeed = maxSpeed
	params.userData = actor
	params.collisionQueryRange = params.radius * 12f
	params.pathOptimizationRange = params.radius * 30f
	params.separationWeight = 2f
	params.obstacleAvoidanceType =
		3 // Don't know what this represents but got it from a working demo.  I assume it's two bitwise flags.
	params.updateFlags =
		CrowdAgentParams.DT_CROWD_ANTICIPATE_TURNS or CrowdAgentParams.DT_CROWD_OPTIMIZE_VIS or CrowdAgentParams.DT_CROWD_OPTIMIZE_TOPO or CrowdAgentParams.DT_CROWD_OBSTACLE_AVOIDANCE

	return params
}

fun mythicToDetour(deck: Deck, navigation: NavigationState): NavigationState {
	val crowd = navigation.crowd
	val spirits = deck.spirits
	val agents = navigation.agents
	val missing = spirits - agents.keys
	val removed = agents
		.filterKeys { !spirits.containsKey(it) || deck.characters[it]?.isAlive != true }

	val newAgents = missing.mapValues { (actor, _) ->
		val body = deck.bodies[actor]!!
		val agent = crowd.addAgent(toRecastVector3(body.translation), newCrowdAgentParams(actor, 1f))
		if (agent.state == CrowdAgent.CrowdAgentState.DT_CROWDAGENT_STATE_INVALID)
			throw Error("Error creating navigation agent")

		assert(agent != null)
		agent!!
	}

	val updated = (agents - removed.keys)

	for ((actor, agent) in updated) {
		val body = deck.bodies[actor]!!
		agent.npos = toRecastVector3(body.translation)
		val targetPosition = spirits[actor]!!.goal.destination
		if (targetPosition != null) {
			val nearest = nearestPolygon(navigation, targetPosition)
			if (nearest != null) {
				val result = crowd.requestMoveTarget(agent, nearest.result.nearestRef, nearest.result.nearestPos)
				if (!result) {
					val k = 0
				}
			}
		}
	}

	for (agent in removed.values) {
		crowd.removeAgent(agent)
	}

	return navigation.copy(
		agents = updated + newAgents
	)
}

private val debugInfo = CrowdAgentDebugInfo()

fun updateNavigation(deck: Deck, delta: Float, navigation: NavigationState): NavigationState {
	val nextNavigation = mythicToDetour(deck, navigation)
	val crowd = nextNavigation.crowd
	crowd.update(delta, debugInfo)
	return nextNavigation
}

fun updateNavigationDirections(navigation: NavigationState): Table<NavigationDirection> =
	navigation.agents.mapValues { (_, agent) ->
		val velocity = fromRecastVector3(agent.vel)
		if (velocity == Vector3.ZERO)
			Vector3.ZERO
		else
			velocity.normalized()
	}
