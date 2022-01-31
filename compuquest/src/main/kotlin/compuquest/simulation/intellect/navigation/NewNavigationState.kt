package compuquest.simulation.intellect.navigation

import godot.Node
import godot.Spatial
import org.recast4j.detour.NavMeshQuery
import silentorb.mythic.godoting.findChildren

fun newNavigationState(collisionObjects: List<Spatial>): NavigationState? {
	val meshes = newNavMeshTriMeshes(collisionObjects)
	val mesh = newNavMesh(meshes)

	return if (mesh == null)
		null
	else
		NavigationState(
			mesh = mesh,
			query = NavMeshQuery(mesh),
			crowd = newCrowd(mesh),
			agents = mapOf()
		)
}

fun newNavigationState(scene: Node): NavigationState? {
	val collisionObjects = findChildren(scene, ::isCollisionObject) as List<Spatial>
	return newNavigationState(collisionObjects)
}