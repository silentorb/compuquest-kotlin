package compuquest.simulation.intellect.navigation

import org.recast4j.detour.NavMesh
import org.recast4j.detour.NavMeshBuilder
import org.recast4j.recast.Heightfield
import org.recast4j.recast.RecastBuilder
import org.recast4j.recast.RecastBuilderConfig
import org.recast4j.recast.geom.TriMesh
import silentorb.mythic.ent.*

var originalNavMeshData: List<TriMesh> = listOf()
var globalHeightMap: Heightfield? = null

fun newNavMesh(meshes: List<TriMesh>): NavMesh? {
  if (meshes.none())
    return null

  val vertices = meshes.flatMap { it.verts.toList() }
  originalNavMeshData = meshes

  val padding = 10f

  val minBounds = floatArrayOf(

      (0 until vertices.size step 3).map { vertices[it] }.firstFloatSortedBy { it } - padding,
      (1 until vertices.size step 3).map { vertices[it] }.firstFloatSortedBy { it } - padding,
      (2 until vertices.size step 3).map { vertices[it] }.firstFloatSortedBy { it } - padding
  )

  val maxBounds = floatArrayOf(
      (0 until vertices.size step 3).map { vertices[it] }.firstFloatSortedByDescending { it } + padding,
      (1 until vertices.size step 3).map { vertices[it] }.firstFloatSortedByDescending { it } + padding,
      (2 until vertices.size step 3).map { vertices[it] }.firstFloatSortedByDescending { it } + padding
  )
  val geometry = GeometryProvider(
      meshes.toMutableList(),
      mutableListOf(),
      minBounds,
      maxBounds
  )

  val recastConfig = newRecastConfig()
  val builderConfig = RecastBuilderConfig(recastConfig, minBounds, maxBounds)
  val builder = RecastBuilder()
  val buildResult = builder.build(geometry, builderConfig)
  val params = newNavMeshDataCreateParams(geometry, buildResult)
  val meshData = NavMeshBuilder.createNavMeshData(params)
  if (meshData == null)
    throw Error("Error generating NavMesh")

  globalHeightMap = buildResult.solidHeightfield
  return NavMesh(meshData, vertsPerPoly, 0)
}
