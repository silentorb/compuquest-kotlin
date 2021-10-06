package compuquest.simulation.happening

import compuquest.simulation.general.*
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

fun joinedPlayerEvents(world: World, previous: World?, events: Events): Events {
  return events
    .filter { it.type == joinedPlayer && it.value is Id }
    .map { Event(deleteBodyCommand, it.target as Id) } +
      dayEvents(world.day, previous?.day)
}

fun eventsFromEvents(world: World, previous: World?, events: Events): Events {
  return events +
      joinedPlayerEvents(world, previous, events)
}
