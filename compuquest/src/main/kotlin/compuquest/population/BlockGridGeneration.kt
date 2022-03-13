package compuquest.population

import compuquest.generation.engine.*
import compuquest.generation.general.*
import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.World
import godot.Material
import godot.Node
import godot.PackedScene
import godot.Spatial
import godot.core.Vector3
import godot.global.GD
import scripts.world.*
import silentorb.mythic.debugging.getDebugInt
import silentorb.mythic.debugging.getDebugString
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

//val defaultBiomeTextures: Map<String, Map<String, String>> = mapOf(
//    Biomes.checkers to mapOf(
//        PlaceholderTextures.floor to Textures.checkersBlackWhite,
//        PlaceholderTextures.wall to Textures.checkersBlackWhite,
//        PlaceholderTextures.ceiling to Textures.checkersBlackWhite,
//    ),
//    Biomes.dungeon to mapOf(
//        PlaceholderTextures.floor to Textures.cobblestone,
//        PlaceholderTextures.wall to Textures.bricks,
//        PlaceholderTextures.ceiling to Textures.bricks,
//    ),
//    Biomes.forest to mapOf(
//        PlaceholderTextures.floor to Textures.grass,
//        PlaceholderTextures.wall to Textures.grass,
//        PlaceholderTextures.ceiling to Textures.grass,
//    ),
//)

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

//fun prepareBlockGraph(graph: Graph, sideNodes: Collection<String>, biomes: Collection<String>): Graph {
//  val truncatedGraph = graph.filter { !sideNodes.contains(it.source) || !isSimpleSideNode(it.source) }
//  val defaultTexture = biomes
//      .mapNotNull { defaultBiomeTextures[it] }
//      .firstOrNull()
//
//  return if (defaultTexture == null)
//    truncatedGraph
//  else {
//    val placeholderEntries = graph.filter { entry ->
//      entry.property == SceneProperties.texture && defaultTexture.containsKey(entry.target)
//    }
//
//    val replacements = placeholderEntries
//        .map { entry -> entry.copy(target = defaultTexture[entry.target]!!) }
//
//    truncatedGraph - placeholderEntries + replacements
//  }
//}

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

//fun blockFromGraph(graph: Graph, cells: Map<Vector3i, BlockCell>, root: String, name: String,
//                   biomes: Collection<String>,
//                   heightOffset: Int): Block {
//  val rotation = getNodeValue<BlockRotations>(graph, root, GameProperties.blockRotations)
//  val rarity = getNodeValue<Int>(graph, root, GameProperties.rarity)
//  val traversable = getTraversable(cells)
//  val blockAttributes = getNodesWithAttribute(graph, root).toSet()
//  return Block(
//      name = name + if (heightOffset != 0) heightOffset else "",
//      cells = cells,
//      traversable = traversable,
//      rotations = rotation ?: BlockRotations.none,
//      biomes = biomes.toSet(),
//      heightOffset = heightOffset,
//      significantCellCount = traversable.size + interpolateTraversibleCellCount(traversable),
//      attributes = blockAttributes,
//      rarity = if (rarity != null)
//        Rarity.values()[rarity - 1]
//      else
//        Rarity.uncommon,
//      isBiomeAdapter = nodeHasAttribute(graph, root, GameAttributes.biomeAdapter)
//  )
//}

fun shouldOmit(cellDirections: List<CellDirection>, keys: Set<CellDirection>): Boolean =
	cellDirections.any { cellDirection ->
		keys.contains(cellDirection)
	}

//fun graphToBlockBuilder(name: String, graph: Graph): List<BlockBuilder> {
//  val root = getGraphRoots(graph).first()
//  val biomes = getNodeValues<String>(graph, root, GameProperties.biome)
//  return if (biomes.none() || biomes.contains(Biomes.hedgeMaze))
//    listOf()
//  else {
//    val heights = listOf(0) + getNodeValues(graph, root, GameProperties.heightVariant)
//    val sideNodes = getSideNodes(graph)
//
//    val sides = gatherSides(sideGroups, graph, sideNodes, nonTraversableBlockSides)
//    return heights.map { height ->
//      val cellDirectionsMap = mapSideHeightAdjustments(sides, height)
//      val adjustedSides = adjustSideHeights(sides, cellDirectionsMap, height)
//      val cells = cellsFromSides(adjustedSides)
//      assert(cells.keys.contains(Vector3i.zero))
//      val block = blockFromGraph(graph, cells, root, name, biomes, height)
//      val finalGraph = prepareBlockGraph(graph, sideNodes, biomes)
//      block to builderFromGraph(finalGraph, cellDirectionsMap, height)
//    }
//  }
//}

//fun graphsToBlockBuilders(graphLibrary: GraphLibrary): List<BlockBuilder> {
//  val library = newExpansionLibrary(graphLibrary, mapOf())
//  return graphLibrary
//      .filterValues { graph ->
//        graph.any(::isBlockSide)
//      }
//      .keys
//      .flatMap { key ->
//        val expanded = applyCellDirectionOffsets(expandGameInstances(library, key))
//        graphToBlockBuilder(key, expanded)
//      }
//}

fun newGenerationSeed(): Long =
	getDebugString("GENERATION_SEED")?.toLong() ?: System.currentTimeMillis()

fun newGenerationConfig(
	definitions: Definitions,
	seed: Long = newGenerationSeed()
): GenerationConfig {

	return GenerationConfig(
		seed = seed,
		definitions = definitions,
		includeEnemies = getDebugString("MONSTER_LIMIT") != "0",
		cellCount = getDebugInt("BASE_ROOM_COUNT") ?: 50,
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
		}
	}
}

fun applyBiomeTextures(root: Node) {
	val props = findChildrenOfType<PropMesh>(root)
	if (props.any()) {
		val material = GD.load<Material>("res://assets/materials/dev/prototype-grid.tres")!!
		for (prop in props) {
			prop.setSurfaceMaterial(0L, material)
		}
	}
}

fun newBuilder(scene: PackedScene): Builder {
	return { input ->
		val root = scene.instance() as Spatial
		filterConditionalNodes(root, input.neighbors)
		findSideNodes(root).forEach { it.queueFree() }
		applyBiomeTextures(root)
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

fun generateWorld(world: World, worldGenerators: Collection<WorldGenerator>): Pair<BlockGrid, GenerationBundle> =
	if (worldGenerators.none())
		mapOf<Vector3i, GridCell>() to emptyGenerationBundle
	else {
		val (blocks, builders) = gatherBlockBuilders(blocksDirectoryPath)
		val generator = worldGenerators.first()
		val generationConfig = newGenerationConfig(world.definitions, newGenerationSeed())
		val dice = Dice(generationConfig.seed)
		generateWorldBlocks(dice, generationConfig, blocks, builders)
	}
