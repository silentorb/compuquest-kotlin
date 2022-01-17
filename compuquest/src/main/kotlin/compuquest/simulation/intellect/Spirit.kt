package compuquest.simulation.intellect

import godot.core.Vector3
import silentorb.mythic.ent.Id

data class Spirit(
	val actionChanceAccumulator: Int = 0,
	val focusedAction: Id? = null,
	val target: Id? = null,
	val nextDestination: Vector3? = null,
	val lastKnownTargetLocation: Vector3? = null,
	val readyToUseAction: Boolean = false,
)
