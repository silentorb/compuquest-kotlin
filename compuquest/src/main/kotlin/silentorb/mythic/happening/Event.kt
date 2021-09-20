package silentorb.mythic.happening

import silentorb.mythic.ent.Key

data class Event(
  val type: Key,
  val target: Any? = null,
  val value: Any? = null,
)

typealias Events = List<Event>

inline fun <reified T>filterEventValues(type: Key, events: Events) =
  events
    .filter { it.type == type}
    .mapNotNull { it.value as? T }

inline fun <reified T>filterEventTargets(type: Key, events: Events) =
  events
    .filter { it.type == type}
    .mapNotNull { it.target as? T }

fun <T> handleEvents(handler: (Event, T) -> T): (Events, T) -> T = { events, initial ->
  events.fold(initial) { a, b -> handler(b, a) }
}
