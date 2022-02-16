package compuquest.simulation.intellect.navigation

import compuquest.simulation.characters.defaultCharacterHeight
import compuquest.simulation.characters.defaultCharacterRadius
import org.recast4j.detour.NavMeshDataCreateParams
import org.recast4j.detour.crowd.CrowdAgentParams
import org.recast4j.recast.RecastBuilder
import org.recast4j.recast.RecastConfig
import org.recast4j.recast.RecastConstants
import org.recast4j.recast.geom.InputGeomProvider

const val cellSize = 0.1f
const val cellHeight = cellSize
const val agentHeight = defaultCharacterHeight
const val agentRadius = defaultCharacterRadius + 0.02f
const val maxAgentRadius = agentRadius * 4
const val agentMaxClimb = 0.3f

const val vertsPerPoly = 6

fun newRecastConfig() =
	RecastConfig(
		RecastConstants.PartitionType.WATERSHED,
		cellSize,
		cellHeight,
		45f,
		true,
		true,
		true,
		agentHeight,
		agentRadius,
		agentMaxClimb,
		3,
		20,
		12f,
		1.3f,
		vertsPerPoly,
		6f,
		1f,
		walkable,
		true
	)

fun newNavMeshDataCreateParams(
	geometry: InputGeomProvider,
	builderResult: RecastBuilder.RecastBuilderResult
): NavMeshDataCreateParams {
	val mesh = builderResult.mesh
	val params = NavMeshDataCreateParams()
	params.verts = mesh.verts
	params.vertCount = mesh.nverts
	params.polys = mesh.polys
	params.polyAreas = mesh.areas
//  params.polyFlags = mesh.flags
	params.polyFlags = mesh.polys.map { 1 }.toIntArray()
	params.polyCount = mesh.npolys
	params.nvp = mesh.nvp
	val detailedMesh = builderResult.getMeshDetail()
	if (detailedMesh != null) {
		params.detailMeshes = detailedMesh.meshes
		params.detailVerts = detailedMesh.verts
		params.detailVertsCount = detailedMesh.nverts
		params.detailTris = detailedMesh.tris
		params.detailTriCount = detailedMesh.ntris
	}
	params.cs = cellSize
	params.ch = cellHeight
	params.walkableHeight = agentHeight
	params.walkableRadius = agentRadius
	params.walkableClimb = agentMaxClimb
	params.bmin = mesh.bmin
	params.bmax = mesh.bmax
	params.buildBvTree = false
	return params
}

fun newCrowdAgentParams(
	maxSpeed: Float = 1f,
	radius: Float = agentRadius,
	height: Float = agentHeight
): CrowdAgentParams {
	val params = CrowdAgentParams()

	params.radius = radius
	params.height = height
	params.maxAcceleration = 8f
	params.maxSpeed = maxSpeed
	params.collisionQueryRange = params.radius * 12f
	params.pathOptimizationRange = params.radius * 30f
	params.separationWeight = 2f

	// Recast preallocates 8 avoidance configurations with default settings
	// This is the index to which of those configuration to use
	// Currently none of those configurations are being adjusted from the defaults
	// so it doesn't matter whether this index is 0 - 7
	params.obstacleAvoidanceType = 0

	params.updateFlags =
		CrowdAgentParams.DT_CROWD_ANTICIPATE_TURNS or CrowdAgentParams.DT_CROWD_OPTIMIZE_VIS or CrowdAgentParams.DT_CROWD_OPTIMIZE_TOPO or CrowdAgentParams.DT_CROWD_OBSTACLE_AVOIDANCE

	return params
}
