package compuquest.generation.engine

import godot.Shape

typealias MeshName = String

data class ArchitectureMeshInfo(
	val shape: Shape?
)

typealias MeshInfoMap = Map<MeshName, ArchitectureMeshInfo>

typealias MeshShapeMap = Map<MeshName, Shape>
