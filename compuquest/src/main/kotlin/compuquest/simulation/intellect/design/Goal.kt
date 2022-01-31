package compuquest.simulation.intellect.design

import godot.core.Vector3
import silentorb.mythic.ent.Id

enum class GoalType {

}

// Currently used as a specialized composite of multiple goals and states with hard-coded priority.
// May eventually be split into a more general system of multiple goals, or a hybrid in that direction
// because too often I've ran into problems with over-generalized AI systems.
data class Goal(
//	val type: GoalType,
	val readyToUseAction: Boolean = false,
	val focusedAction: Id? = null,
	val targetEntity: Id? = null,

	// Location that needs to be reached before performing a given action.
	// Mostly used to get within action range.
	// Temporarily overrides ultimateDestination
	val immediateDestination: Vector3? = null,

	// Used to define a long-term destination and how to get there.
	// May eventually be replaced by a more general list of goals
	val pathDestinations: List<Vector3> = listOf(),
)

typealias Goals = List<Goal>
