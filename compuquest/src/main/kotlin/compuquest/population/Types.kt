package compuquest.population

import compuquest.generation.engine.GenerationConfig
import compuquest.simulation.general.Hands
import compuquest.simulation.general.PreWorld
import silentorb.mythic.randomly.Dice

object MeshAttributes {
	const val ceiling = "ceiling"
	const val floor = "floor"
	const val wall = "wall"
}

data class DistributionConfig(
	val cellCount: Int,
	val level: Int,
	val dice: Dice,
)

typealias SlotSelector = (DistributionConfig, Slots) -> Slots
typealias HandGenerator = (PreWorld, GenerationConfig, Dice, Transforms) -> Hands
typealias PropFilter = (Collection<String>) -> Boolean

data class Distributor(
	val select: SlotSelector,
	val generate: HandGenerator
)
