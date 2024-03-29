package compuquest.simulation.intellect.design

import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.emptyId
import silentorb.mythic.timing.Frames

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

	// Used to insert gaps between actions and make behavior feel more
	// natural instead of the character always immediately rushing from one action to the next.
	// A value of zero means there is currently no pause.
	val pause: Frames = 0,

	// Has the AI enemy become aware of enemies?
	val isAlerted: Boolean = false,

	// * * * End design-only fields

	// * * * The following fields are used for both design and execution
	val targetEntity: Id = emptyId,

	// * * * End design/execution fields

	// * * * The following fields are used just as output of design and input for execution
	val readyTo: ReadyMode = ReadyMode.none,
	val focusedAction: Id = emptyId,
	val interactionBehavior: Key = "",

	// Indirect location to move toward.
	// Input for navigation.
	val destination: Vector3? = null,

	// * * * End execution fields
)

fun wait(goal: Goal): Goal =
	goal.copy(readyTo = ReadyMode.none)
