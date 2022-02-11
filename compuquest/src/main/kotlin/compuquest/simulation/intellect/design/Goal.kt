package compuquest.simulation.intellect.design

import godot.core.Vector3
import silentorb.mythic.ent.Id

enum class ReadyMode {
	none,
	action,
	interact,
	move,
}

// Currently used as a specialized composite of multiple goals and states with hard-coded priority.
// May eventually be split into a more general system of multiple goals, or a hybrid in that direction
// because too often I've ran into problems with over-generalized AI systems.
data class Goal(
	// * * * The following fields are used just for design

	// Used to define a long-term destination and how to get there.
	// May eventually be replaced by a more general list of goals
	val pathDestinations: List<Vector3> = listOf(),

	// * * * End design-only fields

	// * * * The following fields are used for both design and execution
	val targetEntity: Id? = null,

	// * * * End design/execution fields

	// * * * The following fields are used just as output of design and input for execution
	val readyTo: ReadyMode = ReadyMode.none,
	val focusedAction: Id? = null,

	// Indirect location to move toward.
	// Input for navigation.
	val destination: Vector3? = null,

	// * * * End execution fields
)
