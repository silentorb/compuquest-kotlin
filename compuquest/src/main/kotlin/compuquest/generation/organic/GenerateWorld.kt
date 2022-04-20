package compuquest.generation.organic

import compuquest.generation.engine.GenerationConfig
import compuquest.simulation.general.Hands
import compuquest.simulation.general.PreWorld
import silentorb.mythic.randomly.Dice

fun generateWorld(
	world: PreWorld,
	generationConfig: GenerationConfig
): Hands {
	val dice = Dice(generationConfig.seed)
	val grid = windingPathSimple(dice, generationConfig.cellCount)

	return listOf()
}
