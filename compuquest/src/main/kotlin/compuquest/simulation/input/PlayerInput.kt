package compuquest.simulation.input

import silentorb.mythic.ent.Id

data class PlayerInput(
	val jump: Boolean,
	val lookX: Float,
	val lookY: Float,
	val moveLengthwise: Float,
	val moveLateral: Float,
	val primaryAction: Boolean,
)

typealias PlayerInputs = Map<Id, PlayerInput>

val emptyPlayerInput = PlayerInput(
	jump = false,
	lookX = 0f,
	lookY = 0f,
	moveLengthwise = 0f,
	moveLateral = 0f,
	primaryAction = false,
)
