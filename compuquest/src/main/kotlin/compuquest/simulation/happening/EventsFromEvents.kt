package compuquest.simulation.happening

import compuquest.simulation.general.World
import compuquest.simulation.general.dayEvents
import compuquest.simulation.general.deleteBodyCommand
import compuquest.simulation.general.joinedPlayer
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

fun eventsFromEvents(world: World, previous: World?, events: Events): Events {
  return events + events
    .filter { it.type == joinedPlayer && it.value is Id }
    .map { Event(deleteBodyCommand, it.target as Id) } +
      dayEvents(world.day, previous?.day)
}
