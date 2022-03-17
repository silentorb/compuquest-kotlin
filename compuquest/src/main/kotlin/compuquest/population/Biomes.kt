package compuquest.population

import compuquest.definition.defaultBiomeBlocks
import compuquest.generation.general.*

typealias BiomeBlockNames = Map<String, Set<String>>
typealias BiomeBlocks = Map<String, GroupedBlocks>

fun applyBiomeBlocks(blocks: Collection<Block>): BiomeBlocks =
	defaultBiomeBlocks
		.mapValues { (_, blockNames) ->
			newGroupedBlocks(blocks.filter { blockNames.contains(it.name) || blockNames.contains(it.parent) })
		}
