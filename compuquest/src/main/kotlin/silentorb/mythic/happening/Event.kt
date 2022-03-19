package silentorb.mythic.happening

import silentorb.mythic.ent.Key

data class GenericEvent<T>(
	val type: Key,
	val target: Any? = null,
	val value: T,
)

typealias Event = GenericEvent<Any?>

typealias Events = List<Event>

fun newEvent(type: Key, target: Any? = null, value: Any? = null) =
	Event(type, target, value)

inline fun <reified T> filterEventValues(type: Key, events: Events) =
	events
		.filter { it.type == type }
		.mapNotNull { it.value as? T }

inline fun <reified T> filterEventsByType(type: Key, events: Events): List<GenericEvent<T>> =
	events
		.filter { it.type == type && it.value is T } as List<GenericEvent<T>>

inline fun <reified T> firstEventByType(type: Key, events: Events): GenericEvent<T>? =
	events
		.firstOrNull { it.type == type && it.value is T } as GenericEvent<T>?

inline fun <reified T> filterEventTargets(type: Key, events: Events): List<T> =
	events
		.filter { it.type == type }
		.mapNotNull { it.target as? T }

fun filterEventsByTarget(target: Any, events: Events) =
	events
		.filter { it.target == target }

fun <T> handleEvents(handler: (Event, T) -> T): (Events, T) -> T = { events, initial ->
	events.fold(initial) { a, b -> handler(b, a) }
}

fun hasEvent(events: Events, type: Key, target: Any): Boolean =
	events
		.any { it.type == type && it.target == target }
