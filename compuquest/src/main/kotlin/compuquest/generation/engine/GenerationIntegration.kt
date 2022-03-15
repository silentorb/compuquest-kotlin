package compuquest.generation.engine

import compuquest.generation.general.*
import compuquest.population.applyBiomeBlocks
import silentorb.mythic.randomly.Dice
import silentorb.mythic.spatial.Vector3i

fun newBlockGrid(seed: Long, dice: Dice, firstBlock: Block, blocks: Set<Block>, roomCount: Int): BlockGrid {
  val blockConfig = BlockConfig(
    biomeBlocks = applyBiomeBlocks(blocks),
  )
  return windingPath(seed, dice, blockConfig, roomCount, extractCells(firstBlock, Vector3i.zero, ""))
}
