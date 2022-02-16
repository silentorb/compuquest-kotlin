package compuquest.simulation.intellect.design

import compuquest.simulation.intellect.navigation.NavigationState
import compuquest.simulation.intellect.navigation.getNavigationPath
import godot.core.Vector3

fun moveWithinRange(
	navigation: NavigationState,
	location: Vector3,
	destination: Vector3,
	range: Float,
	goal: Goal,
	onInRange: () -> Goal?
): Goal? {
	val distance = location.distanceTo(destination)
	return if (distance > range) {
		val path = getNavigationPath(navigation, location, destination, range)
		if (true || path.any())
			goal.copy(
				readyTo = ReadyMode.move,
				destination = destination,
			)
		else
			null
	} else
		onInRange()
}
