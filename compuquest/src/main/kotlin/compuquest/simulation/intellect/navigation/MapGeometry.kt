package compuquest.simulation.intellect.navigation

import godot.*
import org.recast4j.recast.AreaModification
import org.recast4j.recast.ConvexVolume
import org.recast4j.recast.geom.InputGeomProvider
import org.recast4j.recast.geom.TriMesh

const val SAMPLE_POLYAREA_TYPE_WALKABLE = 0x3f
val walkable = AreaModification(SAMPLE_POLYAREA_TYPE_WALKABLE)

data class GeometryProvider(
	val _meshes: MutableIterable<TriMesh>,
	val _convexVolumes: MutableIterable<ConvexVolume>,
	val _meshBoundsMin: FloatArray,
	val _meshBoundsMax: FloatArray
) : InputGeomProvider {
	override fun meshes(): MutableIterable<TriMesh> = _meshes
	override fun convexVolumes(): MutableIterable<ConvexVolume> = _convexVolumes
	override fun getMeshBoundsMin(): FloatArray = _meshBoundsMin
	override fun getMeshBoundsMax(): FloatArray = _meshBoundsMax
}

fun getCollisionShape(spatial: Spatial): Shape? =
	when (spatial) {
		is CollisionObject ->
			spatial.getChildren()
				.filterIsInstance<CollisionShape>()
				.firstOrNull()
				?.shape

		is CSGPrimitive -> getCsgShape(spatial)
		else -> null
	}

fun newNavMeshTriMeshes(collisionObjects: List<Spatial>): List<TriMesh> {
	return collisionObjects
		.mapNotNull { collisionObject ->
			val shape = getCollisionShape(collisionObject)
			val mesh = if (shape != null)
				getShapeMesh(shape)
			else
				null

			if (mesh != null) {
				val vertices = mesh.vertices.flatMap {
					val temp = collisionObject.globalTransform.xform(it)
					listOf(temp.x.toFloat(), temp.y.toFloat(), temp.z.toFloat())
				}
					.toFloatArray()

				val faces = mesh.triangles.toIntArray()
				TriMesh(vertices, faces)
			} else
				null
		}
}
