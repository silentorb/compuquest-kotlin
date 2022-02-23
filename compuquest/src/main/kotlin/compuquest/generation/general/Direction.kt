package compuquest.generation.general

import silentorb.mythic.spatial.Vector3i

enum class Direction {
  up,
  down,
  east,
  north,
  west,
  south
}

val verticalDirectionVectors: Map<Direction, Vector3i> = mapOf(
    Direction.up to Vector3i(0, 0, 1),
    Direction.down to Vector3i(0, 0, -1)
)

val horizontalDirectionVectors: Map<Direction, Vector3i> = mapOf(
    Direction.east to Vector3i(1, 0, 0),
    Direction.north to Vector3i(0, 1, 0),
    Direction.west to Vector3i(-1, 0, 0),
    Direction.south to Vector3i(0, -1, 0)
)

val directionVectors: Map<Direction, Vector3i> = verticalDirectionVectors.plus(horizontalDirectionVectors)

val directionVectorsReverseLookup = directionVectors.map { Pair(it.value, it.key) }.associate { it }

val horizontalDirections = horizontalDirectionVectors.keys
val verticalDirections = verticalDirectionVectors.keys
val allDirections = directionVectors.keys
val directionNames = allDirections.map { it.name }

val horizontalDirectionList = listOf(Direction.east, Direction.north, Direction.west, Direction.south)

fun rotateDirection(turns: Int): (Direction) -> Direction = { direction ->
  val index = horizontalDirectionList.indexOf(direction)
  if (index == -1)
    direction
  else {
    val newIndex = (index + 4 + turns) % 4
    horizontalDirectionList[newIndex]
  }
}

val oppositeDirections: Map<Direction, Direction> = mapOf(
    Direction.up to Direction.down,
    Direction.down to Direction.up,
    Direction.west to Direction.east,
    Direction.east to Direction.west,
    Direction.north to Direction.south,
    Direction.south to Direction.north
)

fun isVertical(direction: Direction) =
    direction == Direction.up || direction == Direction.down

fun isHorizontal(direction: Direction) =
    !isVertical(direction)
