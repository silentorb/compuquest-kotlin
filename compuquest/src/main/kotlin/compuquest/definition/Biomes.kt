package compuquest.definition

import compuquest.population.BiomeBlockNames
import compuquest.population.MeshAttributes
import compuquest.population.Textures

object Biomes {
	val checkers = "checkers"
	val city = "city"
	val dungeon = "dungeon"
	val forest = "forest"
	val graveyard = "graveyard"
	val lagoon = "lagoon"
	val hedgeMaze = "hedgeMaze"
	val home = "home"
}

val defaultBiomeTextures: Map<String, Map<String, String>> = mapOf(
	Biomes.dungeon to mapOf(
		MeshAttributes.floor to Textures.dirt,
		MeshAttributes.wall to Textures.grayBricks,
		MeshAttributes.ceiling to Textures.dirt,
	),
	Biomes.graveyard to mapOf(
		MeshAttributes.floor to Textures.cold,
		MeshAttributes.wall to Textures.mausoleumWall,
		MeshAttributes.ceiling to Textures.cold,
	),
	Biomes.lagoon to mapOf(
		MeshAttributes.floor to Textures.greenGrass,
		MeshAttributes.wall to Textures.cliffWall,
		MeshAttributes.ceiling to Textures.greenGrass,
	),
//    Biomes.forest to mapOf(
//        MeshAttribute.floor to Textures.grass,
//        MeshAttribute.wall to Textures.grass,
//        MeshAttribute.ceiling to Textures.grass,
//    ),
)

object Blocks {
	const val diagonal = "diagonal"
	const val home = "home"
	const val lowerFiller = "lowerFiller"
	const val main = "main"
	const val slope = "slope"
}

val commonBiomeBlocks = setOf(
	Blocks.diagonal,
	Blocks.lowerFiller,
	Blocks.main,
	Blocks.slope,
)

val defaultBiomeBlocks: BiomeBlockNames = mapOf(
	Biomes.dungeon to commonBiomeBlocks,
	Biomes.graveyard to commonBiomeBlocks,
	Biomes.lagoon to commonBiomeBlocks,
)
