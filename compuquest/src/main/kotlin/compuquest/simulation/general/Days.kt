package compuquest.simulation.general

import scripts.debugLog
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

const val dayMinutes = 60

data class DayState(
  val accumulator: Int = 0,
  val value: Int = 0,
  val dayLength: Int, // In seconds
)

fun updateDay(day: DayState): DayState {
  val accumulator = day.accumulator + 1
  val dayLength = day.dayLength * 60
  val dayChanged = accumulator >= dayLength
  if (getDebugBoolean("DEBUG_DAYS")) {
    debugLog("DAY: ${day.value} ${day.dayLength} ${day.dayLength * 60} ${day.accumulator}")
  }
  return if (dayChanged)
    day.copy(accumulator = accumulator - dayLength, value = day.value + 1)
  else
    day.copy(accumulator = accumulator)
}

const val newDayEvent = "newDay"

fun dayEvents(next: DayState, previous: DayState?): Events =
  if (previous != null && next.value > previous.value)
    listOf(Event(newDayEvent))
  else
    listOf()
