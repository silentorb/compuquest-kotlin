package compuquest.simulation.input

data class PlayerInput(
	val jump: Boolean,
	val lookHorizontal: Float,
	val lookVertical: Float,
	val moveHorizontal: Float,
	val moveVertical: Float,
	val primaryAction: Boolean,
)
