package compuquest.generation.organic

import compuquest.generation.general.horizontalDirectionVectors
import silentorb.mythic.randomly.Dice
import silentorb.mythic.spatial.Vector3i

tailrec fun addPathStep(dice: Dice, maxSteps: Int, grid: SimpleGrid): SimpleGrid =
	if (grid.size >= maxSteps)
		grid
	else {
		val location = dice.takeOneOrNull(grid)!!
		val options = horizontalDirectionVectors.values
			.mapNotNull { offset ->
				val cell = location + offset
				if (!grid.contains(cell))
					cell
				else
					null
			}

		val nextCell = dice.takeOneOrNull(options)
		val nextGrid = if (nextCell != null)
			grid + nextCell
		else
			grid

		addPathStep(dice, maxSteps, nextGrid)
	}

fun windingPathSimple(dice: Dice, maxSteps: Int): SimpleGrid {
	val grid = setOf(Vector3i.zero)
	return addPathStep(dice, maxSteps, grid)
}
