package compuquest.simulation.intellect.navigation

import org.recast4j.detour.NavMesh
import org.recast4j.detour.crowd.Crowd
import org.recast4j.detour.crowd.CrowdConfig

fun newCrowd(mesh: NavMesh): Crowd {
	val config = CrowdConfig(maxAgentRadius)
	return Crowd(config, mesh)
}
