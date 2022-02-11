package compuquest.simulation.intellect.design

import godot.core.Vector3

fun moveWithinRange(location: Vector3, destination: Vector3, range: Float, goal: Goal, onInRange: () -> Goal?): Goal? {
	val distance = location.distanceTo(destination)
	return if (distance > range)
		goal.copy(
			readyTo = ReadyMode.move,
			destination = destination,
		)
	else
		onInRange()
}
