package compuquest.simulation.happening

import compuquest.simulation.general.Key

data class Event(
  val type: Key,
  val target: Any? = null,
  val value: Any? = null,
)

typealias Events = List<Event>

inline fun <reified T>extractEventValues(type: Key, events: Events) =
  events
    .filter { it.type == type}
    .filterIsInstance<T>()
