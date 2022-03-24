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
		MeshAttributes.ceiling to Textures.grayBricks,
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
	const val upperFiller = "upperFiller"
	const val diagonalUpperFiller = "diagonalUpperFiller"
	const val main = "main"
	const val slope = "slope"

	const val diagonalIndoors = "diagonalIndoors"
	const val mainIndoors = "mainIndoors"
	const val slopeIndoors = "slopeIndoors"
}

val commonBiomeBlocks = setOf(
	Blocks.diagonal,
	Blocks.lowerFiller,
	Blocks.main,
	Blocks.slope,
)

val commonIndoorBiomeBlocks = setOf(
	Blocks.diagonalIndoors,
	Blocks.diagonalUpperFiller,
	Blocks.lowerFiller,
	Blocks.upperFiller,
	Blocks.mainIndoors,
	Blocks.slopeIndoors,
)

val defaultBiomeBlocks: BiomeBlockNames = mapOf(
	Biomes.dungeon to commonIndoorBiomeBlocks,
	Biomes.graveyard to commonBiomeBlocks,
	Biomes.lagoon to commonBiomeBlocks,
)
