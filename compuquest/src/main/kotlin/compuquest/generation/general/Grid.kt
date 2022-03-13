package compuquest.generation.general

import godot.core.Vector3
import silentorb.mythic.spatial.Vector3i
import kotlin.math.floor

const val cellLength = 5f

val cellHalfLength = cellLength / 2f

val cellScale = Vector3(cellLength, cellLength / 4, cellLength)

fun absoluteCellPosition(position: Vector3i): Vector3 =
    position.toVector3() * cellScale

typealias ConnectionPair = Pair<Vector3i, Vector3i>

typealias ConnectionSet = Set<ConnectionPair>

fun containsConnection(connections: ConnectionSet, first: Vector3i, second: Vector3i): Boolean =
    connections.contains(Pair(first, second)) || connections.contains(Pair(second, first))

fun containsConnection(position: Vector3i): (ConnectionPair) -> Boolean = { connection ->
  connection.first == position || connection.second == position
}

fun cellConnections(connections: ConnectionSet, position: Vector3i): List<ConnectionPair> =
    connections.filter(containsConnection(position))

fun cellNeighbors(connections: ConnectionSet, position: Vector3i): List<Vector3i> =
    connections.filter(containsConnection(position))
        .map { connection -> connection.toList().first { it != position } }

fun getPointCell(point: Vector3): Vector3i {
  val offset = (point + cellHalfLength) / cellLength
  return Vector3i(
      floor(offset.x.toDouble()).toInt(),
      floor(offset.y.toDouble()).toInt(),
      floor(offset.z.toDouble() / 4).toInt()
  )
}

fun getCellPoint(cell: Vector3i): Vector3 =
    absoluteCellPosition(cell)

//fun cellSlots(location: Vector3i, cell: Cell): List<Vector3> {
//  val point = getCellPoint(location)
//  return cell.slots.map { point + it }
//}
