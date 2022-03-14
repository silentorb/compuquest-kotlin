package compuquest.simulation.intellect.navigation

import godot.*
import org.recast4j.recast.AreaModification
import org.recast4j.recast.ConvexVolume
import org.recast4j.recast.geom.InputGeomProvider
import org.recast4j.recast.geom.TriMesh
import silentorb.mythic.godoting.findChildrenOfType

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

fun getCollisionShape(spatial: Spatial): IntermediateMesh? =
	when (spatial) {
		is CollisionObject -> {
			val shape = spatial.getChildren()
				.filterIsInstance<CollisionShape>()
				.firstOrNull()
				?.shape

			if (shape is ConvexPolygonShape) {
				val meshInstance = findChildrenOfType<MeshInstance>(spatial).firstOrNull()
				val trimeshShape = meshInstance
					?.mesh?.createTrimeshShape() as? ConcavePolygonShape

				if (trimeshShape != null)
					meshShapeVertices(trimeshShape, meshInstance.scale)
				else
					null
			} else
				getShapeMesh(shape)
		}

		is CSGPrimitive -> getShapeMesh(getCsgShape(spatial))
		else -> null
	}

fun collisionObjectsToIntermediateMeshes(collisionObjects: List<Spatial>): List<IntermediateMesh> =
	collisionObjects
		.mapNotNull { collisionObject ->
			val mesh = getCollisionShape(collisionObject)
			mesh?.copy(
				vertices = mesh.vertices.map {
					collisionObject.globalTransform.xform(it)
				}
			)
		}

fun newNavMeshTriMeshes(collisionObjects: List<Spatial>): List<TriMesh> =
	collisionObjectsToIntermediateMeshes(collisionObjects)
		.map { mesh ->
			val vertices = mesh.vertices.flatMap {
				listOf(it.x.toFloat(), it.y.toFloat(), it.z.toFloat())
			}
				.toFloatArray()

			val faces = mesh.triangles.toIntArray()
			TriMesh(vertices, faces)
		}
