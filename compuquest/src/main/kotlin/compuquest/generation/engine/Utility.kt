package compuquest.generation.engine

import compuquest.generation.general.*
import godot.core.Vector3
import silentorb.mythic.spatial.Pi

//fun applyBiomesToGrid(grid: MapGrid, biomeGrid: BiomeGrid): CellBiomeMap =
//    grid.cells.mapValues { (cell, _) ->
//      biomeGrid(absoluteCellPosition(cell))
//    }

fun splitBlockBuilders(blockBuilders: Collection<BlockBuilder>): Pair<Set<Block>, Map<String, Builder>> =
    Pair(
        blockBuilders.map { it.first }.toSet(),
        blockBuilders.associate { Pair(it.first.name, it.second) }
    )

fun squareOffsets(length: Int): List<Vector3> {
  val step = cellLength / length
  val start = step / 2f

  return (0 until length).flatMap { y ->
    (0 until length).map { x ->
      Vector3(start + step * x - cellHalfLength, start + step * y - cellHalfLength, -cellHalfLength)
    }
  }
}

//fun newArchitectureMesh(meshes: MeshInfoMap, depiction: Depiction, position: Vector3,
//                        orientation: Quaternion = Quaternion(),
//                        scale: Vector3 = Vector3.unit): Hand {
//  val meshInfo = meshes[depiction.mesh]
//  val shape = meshInfo?.shape
//  return Hand(
//      body = Body(
//          position = position,
//          orientation = orientation,
//          scale = scale
//      ),
//      collisionShape = if (shape != null)
//        CollisionObject(
//            shape = shape,
//            groups = CollisionGroups.solidStatic,
//            mask = CollisionGroups.staticMask
//        )
//      else
//        null,
//      depiction = depiction
//  )
//}

typealias VerticalAligner = (Float) -> (Float)

val alignWithCeiling: VerticalAligner = { height -> -height / 2f }
val alignWithFloor: VerticalAligner = { height -> height / 2f }

//fun align(meshInfo: MeshInfoMap, aligner: VerticalAligner) = { mesh: MeshName? ->
//  val height = meshInfo[mesh]?.shape?.height
//  if (height != null)
//    Vector3(0f, 0f, aligner(height))
//  else
//    Vector3.ZERO
//}

fun applyTurnsOld(turns: Int): Float =
    (turns.toFloat() - 1) * Pi * 0.5f

fun applyTurns(turns: Int): Float =
    turns.toFloat() * Pi * 0.5f

tailrec fun expandSideGroups(sideGroups: Map<String, Set<String>>, value: Collection<String>, step: Int = 0): Collection<String> {
  if (step > 20)
    throw Error("Infinite loop detected with expanding side type groups")

  val groups = sideGroups.keys.intersect(value)
  return if (groups.none())
    value
  else {
    val next = (value - groups) + groups.flatMap { sideGroups[it]!! }
    expandSideGroups(sideGroups, next, step + 1)
  }
}

//fun getCellDirection(graph: Graph, node: String): CellDirection? =
//    getNodeValue<CellDirection>(graph, node, GameProperties.direction)

fun directionRotation(direction: Direction): Float =
  when (direction) {
    Direction.east -> 0f
    Direction.north -> quarterAngle
    Direction.west -> Pi
    Direction.south -> Pi * 1.5f
    else -> throw Error("Not supported")
  }
