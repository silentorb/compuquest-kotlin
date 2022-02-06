package compuquest.simulation.input

import silentorb.mythic.ent.Id

enum class ActionChange {
	noChange,
	previous,
	next,
}

data class PlayerInput(
	val jump: Boolean,
	val lookX: Float,
	val lookY: Float,
	val moveLengthwise: Float,
	val moveLateral: Float,
	val primaryAction: Boolean,
	val interact: Boolean,
	val actionChange: ActionChange,
)

typealias PlayerInputs = Map<Id, PlayerInput>

val emptyPlayerInput = PlayerInput(
	jump = false,
	lookX = 0f,
	lookY = 0f,
	moveLengthwise = 0f,
	moveLateral = 0f,
	primaryAction = false,
	interact = false,
	actionChange = ActionChange.noChange,
)
