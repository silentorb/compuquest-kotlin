package silentorb.mythic.timing

import compuquest.simulation.updating.simulationFps
import silentorb.mythic.ent.*
import silentorb.mythic.happening.Events
import kotlin.math.ceil

typealias Seconds = Float

data class IntTimer(
	val duration: Int,
	val remaining: Int = duration,
	val onFinished: Events = listOf(),
)

fun floatToIntTime(value: Float): Int =
	ceil((value * simulationFps.toFloat())).toInt()

fun newTimer(duration: Seconds, onFinished: Events = listOf()) =
	IntTimer(
		duration = floatToIntTime(duration),
		onFinished = onFinished,
	)

val updateTimer: (IntTimer) -> IntTimer = { timer ->
	timer.copy(
		remaining = timer.remaining - 1
	)
}

//data class FloatTimer(
//	val duration: Float,
//	val original: Float = duration,
//	val onFinished: Events = listOf(),
//)

//fun updateTimer(delta: Float): (FloatTimer) -> FloatTimer = { timer ->
//	timer.copy(
//		duration = timer.duration - delta
//	)
//}

//fun expiredTimers(timersFloat: Table<FloatTimer>, timersInt: Table<IntTimer>): Set<Id> =
//	setOf<Id>()
//		.plus(
//			timersFloat
//				.filter { it.value.duration < 0f }
//				.keys
//		)
//		.plus(
//			timersInt
//				.filter { it.value.duration < 0 }
//				.keys
//		)

fun expiredTimers(timers: Table<IntTimer>): Set<Id> =
	timers
		.filter { it.value.remaining < 0 }
		.keys

fun eventsFromTimers(timers: Table<IntTimer>): Events =
	timers
		.filter { it.value.remaining < 0 }
		.flatMap { it.value.onFinished }
