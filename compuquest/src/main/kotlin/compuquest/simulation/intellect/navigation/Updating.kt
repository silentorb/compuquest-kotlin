package compuquest.simulation.intellect.navigation

import compuquest.simulation.general.Deck
import compuquest.simulation.general.interactionMaxDistance
import godot.core.Vector3
import org.recast4j.detour.crowd.debug.CrowdAgentDebugInfo
import silentorb.mythic.ent.Table

fun mythicToDetour(deck: Deck, previous: Deck, navigation: NavigationState): NavigationState {
	val crowd = navigation.crowd
	val spirits = deck.spirits
	val characters = deck.characters
	val agents = navigation.agents
	val allAgents = characters.keys + deck.bodies.filter { it.value is CrowdAgentNode }.keys
	val missing = allAgents - agents.keys
	val removed = agents
		.filterKeys { !deck.bodies.containsKey(it) || deck.characters[it]?.isAlive != true }

	val newAgents = missing.associateWith { actor ->
		val body = deck.bodies[actor]!!
		val crowdAgentNode = body as? CrowdAgentNode
		val params = crowdAgentNode?.getCrowdAgentParams() ?: newCrowdAgentParams()
		params.userData = actor
		val agent = crowd.addAgent(toRecastVector3(body.translation), params)
//		if (agent.state == CrowdAgent.CrowdAgentState.DT_CROWDAGENT_STATE_INVALID)
//			throw Error("Error creating navigation agent")

		assert(agent != null)
		agent!!
	}

	val updated = (agents - removed.keys)

	for ((actor, agent) in updated) {
		val body = deck.bodies[actor]!!
		val location = body.globalTransform.origin
		agent.npos = toRecastVector3(location)
		val targetPosition = spirits[actor]?.goal?.destination
		val previousTargetPosition = previous.spirits[actor]?.goal?.destination
		if (targetPosition != previousTargetPosition) {
			if (targetPosition != null) {
				val nearest = nearestPolygon(navigation, targetPosition)
				if (nearest != null) {
					val result = crowd.requestMoveTarget(agent, nearest.result.nearestRef, nearest.result.nearestPos)
					if (!result) {
						val k = 0
					}
				}
			} else {
				crowd.resetMoveTarget(agent)
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

fun updateNavigation(deck: Deck, previous: Deck, delta: Float, navigation: NavigationState): NavigationState {
	val nextNavigation = mythicToDetour(deck, previous, navigation)
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
