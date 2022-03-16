package compuquest.generation.engine

import compuquest.generation.general.Block
import compuquest.generation.general.BlockGrid
import compuquest.generation.general.CellDirection
import compuquest.generation.general.GridCell
import compuquest.population.MaterialMap
import compuquest.simulation.characters.Group
import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.Hands
import godot.Spatial
import silentorb.mythic.ent.Table
import silentorb.mythic.randomly.Dice
import silentorb.mythic.spatial.Pi
import silentorb.mythic.spatial.Vector3i

const val quarterAngle = Pi * 0.5f

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

data class GenerationConfig(
	val seed: Long,
	val definitions: Definitions,
//	val meshes: MeshInfoMap,
//    val resourceInfo: ResourceInfo,
	val includeEnemies: Boolean,
	val cellCount: Int,
//    val hands: Table<NewHand> = mapOf(),
//	val propGroups: Map<String, Set<String>>,
	val level: Int = 1,
	val materials: MaterialMap,
	val groups: Table<Group>,
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
	val location: Vector3i,
	val cell: GridCell,
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
