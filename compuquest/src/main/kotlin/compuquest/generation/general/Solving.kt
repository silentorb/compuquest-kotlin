package compuquest.generation.general

import compuquest.definition.traversableSides
import silentorb.mythic.randomly.Dice
import silentorb.mythic.randomly.getAliasedIndex
import silentorb.mythic.spatial.Vector3i

typealias GetBlock = (Vector3i) -> BlockCell?

fun getOppositeCellDirection(cellDirection: CellDirection): CellDirection {
	val oppositeSide = oppositeDirections[cellDirection.direction]!!
	val offset = directionVectors[cellDirection.direction]!!
	val position = cellDirection.cell + offset
	return CellDirection(position, oppositeSide)
}

fun getOtherSide(getBlock: GetBlock, origin: Vector3i): (Direction) -> Side? = { direction ->
	val oppositeSide = oppositeDirections[direction]!!
	val offset = directionVectors[direction]!!
	val position = origin + offset
	val block = getBlock(position)
	block?.sides?.getOrDefault(oppositeSide, null)
}

fun sidesMatch(first: Side, second: Side, isEssential: Boolean = true): Boolean =
	first.other.contains(second.mine) &&
			second.other.contains(first.mine) &&
			(!isEssential || (traversableSides.contains(first.mine) && traversableSides.contains(second.mine)))

fun sidesMatch(surroundingSides: SideMap, direction: Direction, blockSide: Side, isEssential: Boolean = true): Boolean {
	val otherSide = surroundingSides[direction]
	return otherSide != null && sidesMatch(blockSide, otherSide, isEssential)
}

fun getSurroundingSides(getBlock: GetBlock, location: Vector3i): SideMap {
	val getOther = getOtherSide(getBlock, location)
	return allDirections
		.mapNotNull {
			val side = getOther(it)
			if (side == null)
				null
			else
				it to side
		}
		.associate { it }
}

fun getSurroundingSides(blockGrid: BlockGrid, location: Vector3i): SideMap {
	val getBlock: GetBlock = { blockGrid[it]?.cell }
	return getSurroundingSides(getBlock, location)
}

// essentialDirectionSide is from the perspective of the potential block
fun checkBlockMatch(
	surroundingSides: SideMap, getBlock: GetBlock,
	essentialDirectionSide: Direction?
): (Block) -> Vector3i? = { block ->
	val traversable = block.traversable.any()
	val blockCells = if (traversable)
		block.traversable
	else
		block.cells.keys

	val match = blockCells
		.firstOrNull { baseOffset ->
			val essentialCell = block.cells[baseOffset]
			(essentialDirectionSide == null ||
					essentialCell?.sides?.getOrDefault(essentialDirectionSide, null)?.isTraversable == traversable) &&
					block.cells
						.all { (cellOffset, cell) ->
							val appliedOffset = cellOffset - baseOffset
							if (getBlock(appliedOffset) != null)
								false
							else {
								val surroundingSides2 = if (appliedOffset == Vector3i.zero)
									surroundingSides
								else
									getSurroundingSides(getBlock, appliedOffset)

								val isEssentialCell = cellOffset == baseOffset
								val result = surroundingSides2.all { side ->
									sidesMatch(cell.sides, side.key, side.value, isEssentialCell && side.key == essentialDirectionSide)
								}
								result
							}
						}
		}

	match
}

fun matchBlock(
	dice: Dice, blocks: Set<Block>, getBlock: GetBlock, surroundingSides: SideMap,
	essentialDirectionSide: Direction?
): Pair<Vector3i, Block>? {
	val options = blocks.mapNotNull { block ->
		val offset = checkBlockMatch(surroundingSides, getBlock, essentialDirectionSide)(block)
		if (offset != null)
			offset to block
		else
			null
	}
	return if (options.none())
		null
	else {
		val rarities = options.map { it.second.rarity }.distinct()
		val rarityTable = newRarityTable(rarities)
		val rarity = rarities[getAliasedIndex(rarityTable, dice)]
		val rarityBlocks = options.filter { it.second.rarity == rarity }
		dice.takeOne(rarityBlocks)
	}
}

fun matchConnectingBlock(
	dice: Dice, blocks: Set<Block>, grid: BlockGrid, location: Vector3i,
	essentialDirectionSide: Direction?
): Pair<Vector3i, Block>? {
	val surroundingSides = getSurroundingSides(grid, location)
	val getBlock: GetBlock = { grid[it + location]?.cell }
	return matchBlock(dice, blocks, getBlock, surroundingSides, essentialDirectionSide)
}

fun openSides(blockGrid: BlockGrid, position: Vector3i, condition: (Side) -> Boolean): Map<Direction, Vector3i> {
	val block = blockGrid[position]!!
	return directionVectors
		.filter { direction ->
			val side = block.cell.sides[direction.key]
			side != null && condition(side) && !blockGrid.containsKey(position + direction.value)
		}
}

fun filterBlockSides(
	blockGrid: BlockGrid, position: Vector3i, condition: (Side) -> Boolean = { it.isTraversable }
): List<CellDirection> {
	val options = openSides(blockGrid, position, condition)
	return options.map {
		CellDirection(
			cell = position,
			direction = it.key,
		)
	}
}

fun getIncompleteBlockSides(
	blockGrid: BlockGrid,
	condition: (Side) -> Boolean = { it.isTraversable }
): List<CellDirection> =
	blockGrid.keys.flatMap { filterBlockSides(blockGrid, it, condition) }

fun filterUsedUniqueBlocks(grid: BlockGrid, blocks: Collection<Block>): Set<Block> {
	val uniqueNames = grid
		.filter { it.value.source.attributes.contains(BlockAttributes.unique) }
		.values
		.map { it.source.name }
		.toSet()

	return blocks
		.filter { !uniqueNames.contains(it.name) }
		.toSet()
}
