package compuquest.generation.engine

import compuquest.generation.general.*
import silentorb.mythic.spatial.*

fun gatherNeighbors(grid: BlockGrid, block: Block, position: Vector3i): Map<CellDirection, String> =
    block.cells.keys
        .flatMap { cell ->
          val cellOffset = position + cell
          allDirections
              .mapNotNull { direction ->
                val rotated = rotateDirection(block.turns)(direction)
                val offset = directionVectors[rotated]!!
                val other = grid[cellOffset + offset]
                val reverse = oppositeDirections[rotated]
                val side = other?.cell?.sides?.getOrDefault(reverse, null)
                val contract = side?.mine
                if (contract != null) {
                  val rotatedCell = rotateZ(-block.turns, cell)
                  CellDirection(rotatedCell, direction) to contract
                } else
                  null
              }
        }
        .associate { it }

fun transformBlockOutput(block: Block, position: Vector3i, bundle: GenerationBundle): GenerationBundle {
  val rotation = (block.turns.toFloat()) * quarterAngle
  val location = absoluteCellPosition(position)
  return bundle.copy(
    spatials = bundle.spatials.map {
      it.translate(location)
      it.rotateY(rotation.toDouble())
      it
    }
  )
//  val parentTransform = integrateTransform(location, rotation)
//  val roots = getGraphRoots(graph)
//  val rootTransforms = roots.flatMap { root ->
//    val localTransform = getAbsoluteNodeTransform(graph, root)
//    listOf(
//        Entry(root, SceneProperties.transform, parentTransform * localTransform),
//    )
//  }
//  return replaceValues(graph, rootTransforms)
}

fun buildBlockCell(general: ArchitectureInput, block: Block, builder: Builder, location: Vector3i): GenerationBundle {
  val grid = general.blockGrid
  val neighbors = gatherNeighbors(grid, block, location)
  val input = BuilderInput(
      general = general,
      neighbors = neighbors,
      turns = block.turns,
      height = block.heightOffset,
  )
  val bundle = builder(input) as GenerationBundle
  val result = transformBlockOutput(block, location, bundle)
  return result
}

fun buildArchitecture(general: ArchitectureInput, builders: Map<String, Builder>): GenerationBundle {
  val groupedCellHands = general.blockGrid
      .filterValues { it.offset == Vector3i.zero }
      .mapValues { (position, block) ->
        val builder = builders[block.source.name] ?: throw Error("Could not find builder for block")
        buildBlockCell(general, block.source, builder, position)
      }

  val merged = if (groupedCellHands.any())
    groupedCellHands.values.reduce { a, b -> a + b }
  else
    GenerationBundle()

  return merged
}
