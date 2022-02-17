package compuquest.simulation.intellect.design

import compuquest.simulation.intellect.spiritUpdateInterval

// This function is intended to be chained with the Elvis operator
fun checkGoalPause(goal: Goal): Goal? =
	if (goal.pause > 0)
		goal.copy(
			pause = goal.pause - spiritUpdateInterval,
		)
	else
		null
