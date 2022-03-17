package compuquest.generation.general

import godot.core.Vector3
import silentorb.mythic.spatial.Vector3i

typealias SideMap = Map<Direction, Side>

data class BlockCell(
    val sides: SideMap,
    val isTraversable: Boolean,
    val attributes: Set<CellAttribute> = setOf(),
)

data class Block(
    val name: String = "",
    val cells: Map<Vector3i, BlockCell> = mapOf(),
    val attributes: Set<String> = setOf(),
    val rotations: BlockRotations = BlockRotations.none,
    // Used as a solving optimization when iterating through possible connecting cells within a polyomino
    val traversable: Set<Vector3i> = setOf(),
    val slots: List<Vector3> = listOf(),
    val turns: Int = 0,
    val heightOffset: Int = 0,
    val significantCellCount: Int = traversable.size,
    val rarity: Rarity = Rarity.common,
    val parent: String = "",
) {
  init {
    assert(slots.none { it.x > 5f || it.y > 5f || it.x < -5f || it.y < -5f })
  }
}
