package compuquest.simulation.intellect.navigation

import godot.core.Vector3
import org.recast4j.detour.NavMesh
import org.recast4j.detour.NavMeshQuery
import org.recast4j.detour.crowd.Crowd
import org.recast4j.detour.crowd.CrowdAgent
import org.recast4j.detour.crowd.CrowdAgentParams
import silentorb.mythic.ent.Id

data class NavigationState(
    val mesh: NavMesh,
    val query: NavMeshQuery,
    val crowd: Crowd,
    val agents: Map<Id, CrowdAgent>
)

typealias NavigationDirection = Vector3

interface CrowdAgentNode {
    fun getCrowdAgentParams(): CrowdAgentParams
}
