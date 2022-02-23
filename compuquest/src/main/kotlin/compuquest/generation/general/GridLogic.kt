package compuquest.generation.general

import silentorb.mythic.spatial.Vector3i

val upVector = Vector3i(0, 0, 1)
val downVector = Vector3i(0, 0, -1)

fun padSlopeCells(cells: List<Vector3i>) =
    cells
        .drop(1)
        .dropLast(1)
        .map { cell -> cell + downVector }

fun isSlope(direction: Vector3i) =
    direction.z != 0 && (direction.x != 0 || direction.y != 0)

fun gatherCellsAlongPath(from: Vector3i, direction: Vector3i, distance: Int): List<Vector3i> {
  val primary = (1..distance).map {
    from + direction * distance
  }

  val upperLayer = primary.map { cell -> cell + upVector }
  val twoLayers = primary.plus(upperLayer)

  return if (isSlope(direction))
    twoLayers.plus(padSlopeCells(primary))
  else
    twoLayers
}

//fun isCellPathOpen(grid: MapGrid, from: Vector3i, direction: Vector3i): Boolean {
////  val cells = gatherCellsAlongPath(from, direction, distance)
//  return grid.cells.containsKey(from + direction)
//}
