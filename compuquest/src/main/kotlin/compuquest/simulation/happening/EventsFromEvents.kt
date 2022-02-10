package compuquest.simulation.happening

import compuquest.simulation.characters.getGroupByKey
import compuquest.simulation.combat.attackEvent
import compuquest.simulation.combat.eventsFromAttacks
import compuquest.simulation.general.*
import compuquest.simulation.input.Commands
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.happening.*

inline fun <reified T> mapEvents(type: Key, crossinline transform: (Id, T) -> Events): (Events) -> Events {
	return { events ->
		events
			.filter { it.type == type && it.target is Id && it.value is T }
			.flatMap { event ->
				transform(event.target as Id, event.value as T)
			}
	}
}

fun joinedPlayerEvents(world: World, previous: World?, events: Events): Events {
	return events
		.filter { it.type == joinedPlayer && it.value is Id }
		.map { newEvent(deleteBodyCommand, it.target as Id) }
//  + dayEvents(world.day, previous?.day)
}

fun eventsFromEvents(world: World, previous: World?, events: Events): Events {
	return events +
			joinedPlayerEvents(world, previous, events) +
			listOf(
				mapEvents(tryActionEvent, eventsFromTryAction(world)),
				mapEvents(attackEvent, eventsFromAttacks(world)),
			)
				.fold(events) { a, b -> a + b(a) } +
			filterEventsByType<NewPlayer>(newPlayerEvent, events)
				.groupBy { it.value.index }
				.flatMap { (_, it) ->
					val request = it.first().value
					val faction = request.faction ?: getGroupByKey(world.deck.groups, world.scenario.defaultPlayerFaction)?.key
					if (faction != null)
						newHandEvents(spawnNewPlayer(world, request.index, faction))
					else
						listOf()
				}
}
