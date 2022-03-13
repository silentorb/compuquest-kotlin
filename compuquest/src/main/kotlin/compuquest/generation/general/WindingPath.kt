package compuquest.generation.general

import silentorb.mythic.debugging.conditionalDebugLog
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.randomly.Dice
import silentorb.mythic.spatial.Vector3i

data class GroupedBlocks(
	val traversable: Set<Block>,
	val flexible: Set<Block>,
	val nonTraversable: Set<Block>,
)

fun newGroupedBlocks(blocks: Collection<Block>): GroupedBlocks {
	val (traversable, nonTraversable) = blocks.partition { it.traversable.any() }
	val flexible = traversable
		.filter { block ->
			val traversableCount = block.cells.values
				.sumBy { cell ->
					cell.sides.count {
						it.value.isTraversable
					}
				}
			traversableCount >= 4
		}
		.toSet()

	return GroupedBlocks(
		traversable = traversable.toSet(),
		flexible = flexible,
		nonTraversable = nonTraversable.toSet(),
	)
}

fun filterUsedUniqueBlock(block: Block?, groupedBlocks: GroupedBlocks): GroupedBlocks =
	if (block != null && block.attributes.contains(BlockAttributes.unique))
		GroupedBlocks(
			traversable = groupedBlocks.traversable - block,
			flexible = groupedBlocks.flexible - block,
			nonTraversable = groupedBlocks.nonTraversable - block,
		)
	else
		groupedBlocks

enum class BranchingMode {
	linear,
	open,
}

data class BlockState(
	val grid: BlockGrid,
	val biomeBlocks: Map<String, GroupedBlocks>,
	val biomeGrid: BiomeGrid,
	val branchingMode: BranchingMode,
	val biomeAdapters: Set<Block>,
	val blacklistSides: List<CellDirection> = listOf(),
	val blacklistBlockLocations: Map<Vector3i, List<Block>> = mapOf(),
	val lastCell: Vector3i? = null,
)

const val debugWorldGenerationKey = "DEBUG_WORLD_GENERATION"
val worldGenerationLog = conditionalDebugLog(debugWorldGenerationKey)

fun extractCells(block: Block, position: Vector3i) =
	block.cells.entries
		.associate { (cellOffset, cell) ->
			position + cellOffset to GridCell(
				cell = cell,
				offset = cellOffset,
				source = block,
			)
		}

fun getNextPosition(incompleteSide: CellDirection): Vector3i {
	val offset = directionVectors[incompleteSide.direction]!!
	val position = incompleteSide.cell
	return position + offset
}

fun getSignificantCellCount(grid: BlockGrid): Int =
	grid.entries
		.filter { it.value.offset == Vector3i.zero }
		.sumBy { it.value.source.significantCellCount }

fun getAvailableBlocks(groupedBlocks: GroupedBlocks, incompleteSides: List<CellDirection>, block: Block?): Set<Block> {
	val blocks = if (incompleteSides.size < 2)
		groupedBlocks.flexible
	else
		groupedBlocks.traversable

	return if (block != null && block.attributes.contains(BlockAttributes.hetero))
		blocks - block
	else
		blocks
}

tailrec fun addPathStep(
	maxSteps: Int,
	dice: Dice,
	state: BlockState
): BlockState {
	val grid = state.grid
	val blacklist = state.blacklistSides

	if (grid.size > 1000)
		throw Error("Infinite loop in world generation.")

	val incompleteSides = if (state.branchingMode == BranchingMode.linear && state.lastCell != null) {
		val sides = filterBlockSides(grid, state.lastCell) - blacklist
		if (sides.any())
			sides
		else
			getIncompleteBlockSides(grid) - blacklist
	} else
		getIncompleteBlockSides(grid) - blacklist

	if (incompleteSides.none())
		return state

	val stepCount = getSignificantCellCount(grid)
//  worldGenerationLog {
//    "Grid size: ${grid.size}, Traversable: $stepCount, Required: $required, Optional: $optional"
//  }
	return if (stepCount >= maxSteps)
		state
	else {
		val incompleteSide = dice.takeOne(incompleteSides)
		val nextPosition = getNextPosition(incompleteSide)
		assert(!grid.containsKey(nextPosition))
		val biome = state.biomeGrid(nextPosition)
		val groupedBlocks = state.biomeBlocks[biome]
		if (groupedBlocks == null)
			throw Error("Biome mismatch")

		val blocks = getAvailableBlocks(groupedBlocks, incompleteSides, grid[state.lastCell]?.source)
		val essentialDirectionSideDirection = oppositeDirections[incompleteSide.direction]!!
		val matchResult = matchConnectingBlock(dice, blocks, grid, nextPosition, essentialDirectionSideDirection)
			?: matchConnectingBlock(dice, state.biomeAdapters - blocks, grid, nextPosition, essentialDirectionSideDirection)

		val nextState = if (matchResult == null) {
			worldGenerationLog {
				"Blacklisting: $biome ${incompleteSide.cell} ${incompleteSide.direction}"
			}
			state.copy(
				blacklistSides = state.blacklistSides + incompleteSide
			)
		} else {
			val (blockOffset, block) = matchResult
			worldGenerationLog {
				"Winding Step: $biome ${incompleteSide.cell} ${nextPosition} ${incompleteSide.direction} ${block.name} "
			}
			val cellAdditions = extractCells(block, nextPosition - blockOffset)
//      if (!cellAdditions.containsKey(nextPosition))
//        matchConnectingBlock(dice, groupedBlocks.all - blocks, grid, nextPosition, incompleteSide)

			assert(cellAdditions.containsKey(nextPosition))
			assert(cellAdditions.any { it.value.offset == Vector3i.zero })
			assert(cellAdditions.none { grid.containsKey(it.key) })
			state.copy(
				biomeBlocks = state.biomeBlocks + (biome to filterUsedUniqueBlock(block, groupedBlocks)),
				grid = grid + cellAdditions,
				lastCell = nextPosition,
			)
		}
		addPathStep(maxSteps, dice, nextState)
	}
}

fun newExtensionBlockState(state: BlockState): BlockState {
	val biomeBlocks = state.biomeBlocks.mapValues { (_, group) ->
		group.copy(
			traversable = group.traversable.filter(::isNotGreedy).toSet(),
			nonTraversable = group.nonTraversable.filter(::isNotGreedy).toSet(),
		)
	}
	return BlockState(
		grid = state.grid,
		biomeGrid = state.biomeGrid,
		biomeBlocks = biomeBlocks,
		biomeAdapters = state.biomeAdapters,
		branchingMode = state.branchingMode,
	)
}

tailrec fun extendBlockSides(dice: Dice, state: BlockState): BlockState {
	val grid = state.grid
	val blacklist = state.blacklistSides

	if (grid.size > 1000)
		throw Error("Infinite loop in world generation.")

	val incompleteSides = getIncompleteBlockSides(grid) { it.isGreedy } - blacklist

	if (incompleteSides.none())
		return state

	val incompleteSide = dice.takeOne(incompleteSides)
	val nextPosition = getNextPosition(incompleteSide)
	assert(!grid.containsKey(nextPosition))
	val biome = state.biomeGrid(nextPosition)
	val groupedBlocks = state.biomeBlocks[biome]
	if (groupedBlocks == null)
		throw Error("Biome mismatch")

	val cell = grid[incompleteSide.cell]!!
	val side = cell.cell.sides[incompleteSide.direction]!!
	val blocks = if (side.isTraversable)
		groupedBlocks.traversable
	else
		groupedBlocks.nonTraversable

	val essentialDirectionSideDirection = oppositeDirections[incompleteSide.direction]!!

	val matchResult = matchConnectingBlock(dice, blocks, grid, nextPosition, essentialDirectionSideDirection)

	val nextState = if (matchResult == null) {
		state.copy(
			blacklistSides = state.blacklistSides + incompleteSide
		)
	} else {
		val (blockOffset, block) = matchResult
		worldGenerationLog {
			"Winding Step: ${incompleteSide.cell} ${incompleteSide.direction} ${block.name} "
		}
		val cellAdditions = extractCells(block, nextPosition - blockOffset)

		assert(cellAdditions.containsKey(nextPosition))
		assert(cellAdditions.any { it.value.offset == Vector3i.zero })
		assert(cellAdditions.none { grid.containsKey(it.key) })
		state.copy(
			biomeBlocks = state.biomeBlocks + (biome to filterUsedUniqueBlock(block, groupedBlocks)),
			grid = grid + cellAdditions,
			lastCell = nextPosition,
		)
	}
	return extendBlockSides(dice, nextState)
}

fun windingPath(seed: Long, dice: Dice, config: BlockConfig, length: Int, grid: BlockGrid): BlockGrid {
	val blocks = filterUsedUniqueBlocks(grid, config.blocks)
	val groupedBlocks = config.biomes
		.associateWith { biome ->
			newGroupedBlocks(blocks.filter { it.biomes.contains(biome) && !it.isBiomeAdapter })
		}
		.plus("" to newGroupedBlocks(blocks.filter { it.biomes.none() }))
		.filterValues { it.flexible.any() }

	val biomeGrid = if (groupedBlocks.any()) {
		val biomeAnchors = newBiomeAnchors(
			groupedBlocks.keys, dice,
			worldRadius = length / 3 * cellLength,
			biomeSize = 15f,
			minGap = 2f
		)
		biomeGridFromAnchors(biomeAnchors)
	} else
		{ _ -> "" }

	val state = BlockState(
		grid = grid,
		biomeBlocks = groupedBlocks,
		biomeGrid = biomeGrid,
		branchingMode = BranchingMode.linear,
		biomeAdapters = blocks.filter { it.isBiomeAdapter }.toSet()
	)

	val (firstLength, secondLength) =
		if (getDebugBoolean("LINEAR_MAP"))
			length to 0
		else
			0 to length

	for (i in 0..10) {
		val intermediateState = addPathStep(firstLength, dice, state)
		val nextState = addPathStep(secondLength, dice, intermediateState.copy(branchingMode = BranchingMode.open))
		if (nextState.grid.size >= length)
			return extendBlockSides(dice, newExtensionBlockState(nextState)).grid
		else
			println("Failed to generate world with seed $seed")
	}
	throw Error("Reached maximum failed world generation attempts")
}
