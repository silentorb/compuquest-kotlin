package compuquest.population

import compuquest.generation.engine.*
import compuquest.generation.general.*
import compuquest.simulation.characters.Group
import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.Hands
import compuquest.simulation.general.PreWorld
import godot.*
import godot.core.Vector3
import godot.global.GD
import scripts.world.*
import silentorb.mythic.debugging.getDebugInt
import silentorb.mythic.debugging.getDebugString
import silentorb.mythic.ent.Table
import silentorb.mythic.godoting.*
import silentorb.mythic.randomly.Dice
import silentorb.mythic.spatial.Vector3i

fun getTraversable(cells: Map<Vector3i, BlockCell>) =
	cells
		.filterValues { it.isTraversable }
		.keys

fun rotateBlockBuilder(turns: Int, blockBuilder: BlockBuilder): BlockBuilder =
	if (turns == 0)
		blockBuilder
	else {
		val (block, builder) = blockBuilder
		val cells = block.cells.entries
			.associate { (offset, cell) ->
				val sides = rotateSides(turns)(cell.sides)
				rotateY(turns, offset) to cell.copy(
					sides = sides,
				)
			}

		block.copy(
			name = block.name + "${-turns}turns",
			cells = cells,
			traversable = getTraversable(cells),
			turns = turns
		) to builder
	}

fun explodeBlockMap(blockBuilders: Collection<BlockBuilder>): List<BlockBuilder> {
	assert(blockBuilders.all { it.first.name.isNotEmpty() })
	return blockBuilders
		.flatMap { blockBuilder ->
			when (blockBuilder.first.rotations) {
				BlockRotations.none -> listOf(blockBuilder)
				BlockRotations.once -> listOf(blockBuilder, rotateBlockBuilder(1, blockBuilder))
				BlockRotations.all -> (0..3).mapNotNull { rotateBlockBuilder(it, blockBuilder) }
			}
		}
}

fun cellsFromSides(sides: List<Pair<CellDirection, Side?>>): Map<Vector3i, BlockCell> {
	val cells = sides
		.groupBy { it.first.cell }
		.entries
		.associate { (offset, value) ->
			// Null sides are used to indicate the existence of a non-traversable cell
			val nonNullSides = value
				.filter { it.second != null }
			val sideMap = nonNullSides
				.associate { it.first.direction to it.second!! }

			assert(nonNullSides.size == sideMap.size)

			val isTraversable = sideMap.any { it.value.isTraversable }
			val attributes = setOfNotNull(
				if (isTraversable) CellAttribute.isTraversable else null,
			)
			offset to BlockCell(
				sides = sideMap,
				isTraversable = isTraversable,
				attributes = attributes,
			)
		}

	return cells
}

fun interpolateCellCount(cells: Set<Vector3i>, axis: Int): Int =
	if (cells.none())
		0
	else
		(cells.minOf { it[axis] } + 1 until cells.maxOf { it[axis] })
			.count { i -> cells.none { it[axis] == i } }

// Traversible cells are only marked along edges of a block
// For large blocks, this function pads the traversable cell count
// by including at least some middle cells in the count
fun interpolateTraversibleCellCount(cells: Set<Vector3i>): Int {
	val minimum =
		interpolateCellCount(cells, 0) +
				interpolateCellCount(cells, 1) +
				interpolateCellCount(cells, 2)
	return minimum * 3 / 2
}

fun newGenerationSeed(level: Int): Long =
	getDebugString("GENERATION_SEED")?.toLong()?.plus(level - 1) ?: System.currentTimeMillis()

fun newGenerationConfig(
	definitions: Definitions,
	groups: Table<Group>,
	materials: MaterialMap,
	level: Int,
	seed: Long = newGenerationSeed(level)
): GenerationConfig {

	return GenerationConfig(
		seed = seed,
		definitions = definitions,
		includeEnemies = getDebugString("MONSTER_LIMIT") != "0",
		cellCount = getDebugInt("BASE_ROOM_COUNT") ?: 50,
		materials = materials,
		groups = groups,
		level = level,
	)
}

fun findSideNodes(root: Node) =
	findChildrenOfScriptType("res://entities/world/SideNode.gd", root)

fun parseBlock(scene: Spatial): Block {
	val sides = findSideNodes(scene)
		.flatMap { node ->
			val mine = getString(node, "mine")
			val side = Side(
				mine = mine,
				other = setOf(getString(node, "other")),
				frequency = MatchFrequency.valueOf(getString(node, "frequency")),
				isTraversable = traversableSides.contains(mine),
//				canMatchEssential = getBoolean(node, "canMatchEssential"),
			)
			(0 until getInt(node, "cellHeight")).map { i ->
				val cell = Vector3i.fromVector3(node.get("cell") as Vector3) + Vector3i(0, i, 0)
				val direction = Direction.valueOf(getString(node, "direction"))
				CellDirection(cell, direction) to side
			}
		}
	val blockNode = scene as? BlockNode
	val cells = cellsFromSides(sides)
	return Block(
		name = scene.name,
		traversable = getTraversable(cells),
		rotations = blockNode?.rotations ?: BlockRotations.none,
		cells = cells,
	)
}

fun filterConditionalNodes(node: Node, neighbors: Map<CellDirection, String>) {
	val conditionalNodes = findChildrenOfType<SideCondition>(node)
	for (conditionalNode in conditionalNodes) {
		val cellDirection = CellDirection(Vector3i.fromVector3(conditionalNode.cell), conditionalNode.direction)
		val sideIsNotEmpty = neighbors.keys.contains(cellDirection)
		val condition = conditionalNode.condition
		if (
			(!sideIsNotEmpty && condition == SideCondition.Condition.sideIsNotEmpty) ||
			(sideIsNotEmpty && condition == SideCondition.Condition.sideIsEmpty)
		) {
			conditionalNode.queueFree()
		} else {
			replacePlaceholderNode(conditionalNode)
		}
	}
}

fun applyBiomeTextures(root: Node, biome: String, materials: MaterialMap) {
	val biomeTextures = defaultBiomeTextures[biome]
	if (biomeTextures != null) {
		val props = findChildrenOfScriptType("res://entities/world/PropMesh.gd", root)
			.filterIsInstance<MeshInstance>()

		if (props.any()) {
			for (prop in props) {
				val attribute = getVariantArray<String>(prop, "attributes").firstOrNull()
				if (attribute != null) {
					val texture = biomeTextures[attribute]
					if (texture != null) {
						val material = getMaterial(materials, texture)
						prop.setSurfaceMaterial(0L, material)
					}
				}
			}
		}
	}
}

fun newBuilder(scene: PackedScene): Builder {
	return { input ->
		val root = scene.instance() as Spatial
		filterConditionalNodes(root, input.neighbors)
		findSideNodes(root).forEach { it.queueFree() }
		val biome = input.cell.biome
		applyBiomeTextures(root, biome, input.general.config.materials)
		GenerationBundle(
			spatials = listOf(root),
		)
	}
}

fun loadBlock(filePath: String): BlockBuilder? {
	val scene = GD.load<PackedScene>(filePath)
	return if (scene != null) {
		val tempNode = scene.instance() as Spatial
		val block = parseBlock(tempNode)
		val builder: Builder = newBuilder(scene)
		tempNode.queueFree()
		block to builder
	} else
		null
}

fun gatherBlockBuilders(directoryPath: String): Pair<Set<Block>, Map<String, Builder>> {
	val files = getFilesInDirectory(directoryPath)
	val baseBlockBuilders = files.mapNotNull { loadBlock("$directoryPath/$it") }
	val blockBuilders = explodeBlockMap(baseBlockBuilders)
	return splitBlockBuilders(blockBuilders)
}

fun generateWorldBlocks(
	dice: Dice,
	generationConfig: GenerationConfig,
	blocks: Set<Block>,
	builders: Map<String, Builder>
): Pair<BlockGrid, GenerationBundle> {
	val home = blocks.firstOrNull { it.name == "home" }
	if (home == null)
		throw Error("Could not find home-set block")

	val blockGrid = newBlockGrid(generationConfig.seed, dice, home, blocks - home, generationConfig.cellCount)
	val architectureInput = newArchitectureInput(generationConfig, dice, blockGrid)
	val graph = buildArchitecture(architectureInput, builders)
	return Pair(blockGrid, graph)
}

val blocksDirectoryPath = "res://world/blocks"

fun generateWorld(
	world: PreWorld,
	generationConfig: GenerationConfig
): Hands {
	val (blocks, builders) = gatherBlockBuilders(blocksDirectoryPath)
	val dice = Dice(generationConfig.seed)
	val (grid, generationBundle) = generateWorldBlocks(dice, generationConfig, blocks, builders)
	val scene = world.scene
	for (spatial in generationBundle.spatials) {
		scene.addChild(spatial)
	}
	val population = populateWorld(world, generationConfig, dice)
	val newGenerationBundle = generationBundle.copy(
		hands = generationBundle.hands + population
	)
	return newGenerationBundle.hands
}
