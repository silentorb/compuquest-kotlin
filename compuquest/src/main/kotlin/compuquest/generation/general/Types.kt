package compuquest.generation.general

import godot.core.Vector2
import godot.core.Vector3
import silentorb.mythic.spatial.Vector3i

data class GridCell(
    val cell: BlockCell,
    val offset: Vector3i,
    val source: Block,
)

typealias BlockGrid = Map<Vector3i, GridCell>

data class WorldBoundary(
    val start: Vector3,
    val end: Vector3,
    val padding: Float = 5f
) {
  val dimensions: Vector3
    get() = end - start
}

data class CellDirection(
    val cell: Vector3i,
    val direction: Direction
)

data class BlockConfig(
    val blocks: Set<Block>,
    val biomes: Set<String>,
)

data class VoronoiAnchor2d<T>(
    val position: Vector2,
    val value: T
)

enum class BlockRotations {
    all,
    none,
    once
}

enum class CellAttribute {
    home,
    isTraversable,
}

object BlockAttributes {
    const val hetero = "heteroBlock"
    const val unique = "unique"
}
