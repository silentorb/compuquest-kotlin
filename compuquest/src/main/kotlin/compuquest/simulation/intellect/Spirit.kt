package compuquest.simulation.intellect

import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.randomly.Dice

const val spiritUpdateInterval = 20

data class Spirit(
	val intervalOffset: Int,
	val actionChanceAccumulator: Int = 0,
	val focusedAction: Id? = null,
	val target: Id? = null,
	val nextDestination: Vector3? = null,
	val lastKnownTargetLocation: Vector3? = null,
	val readyToUseAction: Boolean = false,
)

private var newSpiritIntervalStep: Int = 0

fun nextNewSpiritInterval(): Int {
	val value = newSpiritIntervalStep
	newSpiritIntervalStep = if (value >= spiritUpdateInterval)
		0
	else
		value + 1

	return value
}

fun newSpirit(): Spirit =
	Spirit(
		intervalOffset = nextNewSpiritInterval(),
	)

fun getSpiritIntervalStep(step: Long): Int =
	(step % spiritUpdateInterval.toLong()).toInt()
