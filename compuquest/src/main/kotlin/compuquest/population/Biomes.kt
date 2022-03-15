package compuquest.population

import compuquest.generation.general.*

object Biomes {
	val checkers = "checkers"
	val city = "city"
	val dungeon = "dungeon"
	val forest = "forest"
	val lagoon = "lagoon"
	val hedgeMaze = "hedgeMaze"
	val home = "home"
	val tealPalace = "tealPalace"
}

val defaultBiomeTextures: Map<String, Map<String, String>> = mapOf(
	Biomes.lagoon to mapOf(
		MeshAttributes.floor to Textures.grassGreen,
		MeshAttributes.wall to Textures.cliffWall,
		MeshAttributes.ceiling to Textures.grassGreen,
	),
//    Biomes.dungeon to mapOf(
//        MeshAttribute.floor to Textures.cobblestone,
//        MeshAttribute.wall to Textures.bricks,
//        MeshAttribute.ceiling to Textures.bricks,
//    ),
//    Biomes.forest to mapOf(
//        MeshAttribute.floor to Textures.grass,
//        MeshAttribute.wall to Textures.grass,
//        MeshAttribute.ceiling to Textures.grass,
//    ),
)

object Blocks {
	const val diagonal = "diagonal"
	const val home = "home"
	const val main = "main"
	const val slope = "slope"
}

typealias BiomeBlockNames = Map<String, Set<String>>
typealias BiomeBlocks = Map<String, GroupedBlocks>

val commonBiomeBlocks = setOf(
	Blocks.diagonal,
	Blocks.main,
	Blocks.slope,
)

val defaultBiomeBlocks: BiomeBlockNames = mapOf(
	Biomes.lagoon to commonBiomeBlocks,
)

fun applyBiomeBlocks(blocks: Collection<Block>): BiomeBlocks =
	defaultBiomeBlocks
		.mapValues { (_, blockNames) ->
			newGroupedBlocks(blockNames.mapNotNull { name -> blocks.firstOrNull { it.name == name } })
		}
