package compuquest.generation.engine

fun compileArchitectureMeshInfo(shapes: MeshShapeMap): MeshInfoMap {
  return shapes.mapValues { (_, shape) ->
    val info = ArchitectureMeshInfo(
        shape = shape
    )
    info
  }
}
