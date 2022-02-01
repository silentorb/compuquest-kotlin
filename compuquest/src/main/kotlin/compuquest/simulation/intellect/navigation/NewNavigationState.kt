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

fun getSceneCollisionObjects(scene: Node): List<Spatial> =
	findChildren(scene, ::isCollisionObject) as List<Spatial>

fun newNavigationState(scene: Node): NavigationState? {
	val collisionObjects = getSceneCollisionObjects(scene)
	return newNavigationState(collisionObjects)
}
