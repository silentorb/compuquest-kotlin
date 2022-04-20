package compuquest.simulation.intellect.navigation

import godot.*
import godot.core.PoolVector3Array
import godot.core.VariantArray
import godot.core.Vector3
import godot.global.GD
import org.recast4j.detour.NavMesh

private fun getMaterial(): Material =
	GD.load("res://assets/materials/dev/NavigationPreview.tres")!!

fun verticesToMeshInstance(vertexPool: PoolVector3Array, primitive: Long): MeshInstance {
	val material = getMaterial()
	val arrays = VariantArray<Any?>()
	arrays.resize(ArrayMesh.ARRAY_MAX.toInt())
	arrays[ArrayMesh.ARRAY_VERTEX.toInt()] = vertexPool
	val arrayMesh = ArrayMesh()
	arrayMesh.addSurfaceFromArrays(primitive, arrays)
	val meshInstance = MeshInstance()
	meshInstance.mesh = arrayMesh
	meshInstance.setSurfaceMaterial(0L, material)
	return meshInstance
}

fun intermediateMeshToMeshInstance(geometry: List<IntermediateMesh>): MeshInstance {
	val vertices = PoolVector3Array()
	for (intermediateMesh in geometry) {
		for (index in intermediateMesh.triangles) {
			vertices.append(intermediateMesh.vertices[index])
		}
	}
	return verticesToMeshInstance(vertices, Mesh.PRIMITIVE_TRIANGLES)
}

fun generateNavMeshInputVisualization(scene: Node): Node {
	val collisionObjects = getSceneCollisionObjects(scene)
	val geometry = collisionObjectsToIntermediateMeshes(collisionObjects)
	return intermediateMeshToMeshInstance(geometry)
}

fun generateNavMeshVisualization(navMesh: NavMesh): Node {
	val vertices = PoolVector3Array()
	for (i in 0 until navMesh.tileCount) {
		val tile = navMesh.getTile(i)
		val data = tile.data
		val verts = (0 until data.verts.size / 3).map {
			val j = it * 3
			Vector3(data.verts[j + 0], data.verts[j + 1], data.verts[j + 2])
		}
		for (poly in data.polys) {
			for (j in (0 until poly.vertCount - 2)) {
				val k = j// * 1
				println(verts[poly.verts[k + 2]])
				println(verts[poly.verts[k + 1]])
				println(verts[poly.verts[0]])
				vertices.append(verts[poly.verts[k + 2]])
				vertices.append(verts[poly.verts[k + 1]])
				vertices.append(verts[poly.verts[0]])
			}
		}
	}
	return verticesToMeshInstance(vertices, Mesh.PRIMITIVE_TRIANGLES)
}
