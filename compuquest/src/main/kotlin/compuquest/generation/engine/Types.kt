package compuquest.generation.engine

import compuquest.generation.general.Block
import compuquest.generation.general.BlockGrid
import compuquest.generation.general.CellAttribute
import compuquest.generation.general.CellDirection
import compuquest.population.MaterialMap
import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.Hand
import compuquest.simulation.general.Hands
import godot.Spatial
import godot.core.Vector3
import silentorb.mythic.ent.Table
import silentorb.mythic.randomly.Dice
import silentorb.mythic.spatial.Pi
import silentorb.mythic.spatial.Vector3i

const val quarterAngle = Pi * 0.5f

data class BlockElement(
	val target: String,
	val location: Vector3,
	val orientation: Vector3,
	val scale: Vector3
)

data class ImportedAttributes(
	val cell: Vector3i,
	val attributes: List<CellAttribute>
)

data class Polyomino(
	val attributes: List<ImportedAttributes>,
	val cells: List<Vector3i>,
	val elements: List<BlockElement>
)

typealias PolyominoMap = Map<String, Polyomino>

data class GenerationBundle(
	val spatials: List<Spatial> = listOf(),
	val hands: Hands = listOf(),
) {
	operator fun plus(other: GenerationBundle) =
		GenerationBundle(
			spatials = spatials + other.spatials,
			hands = hands + other.hands,
		)
}

val emptyGenerationBundle = GenerationBundle()

data class GenerationConfig(
	val seed: Long,
	val definitions: Definitions,
//	val meshes: MeshInfoMap,
//    val resourceInfo: ResourceInfo,
	val includeEnemies: Boolean,
	val cellCount: Int,
//    val expansionLibrary: ExpansionLibrary,
//    val hands: Table<NewHand> = mapOf(),
//	val propGroups: Map<String, Set<String>>,
//    val propGraphs: Map<String, Graph>,
	val level: Int = 1,
	val materials: MaterialMap,
)

data class ArchitectureInput(
	val config: GenerationConfig,
	val blockGrid: BlockGrid,
	val dice: Dice,
)

fun newArchitectureInput(generationConfig: GenerationConfig, dice: Dice, blockGrid: BlockGrid) =
	ArchitectureInput(
		config = generationConfig,
		blockGrid = blockGrid,
		dice = dice
	)

data class BuilderInput(
	val general: ArchitectureInput,
	val neighbors: Map<CellDirection, String>,
	val turns: Int,
	val height: Int,
	val biome: String,
)

typealias Builder = (BuilderInput) -> GenerationBundle

typealias BlockBuilder = Pair<Block, Builder>

fun mergeBuilders(vararg builders: Builder): Builder {
	return { input ->
		builders.fold(GenerationBundle()) { a, b -> a + b(input) }
	}
}

operator fun Builder.plus(builder: Builder): Builder =
	mergeBuilders(this, builder)
