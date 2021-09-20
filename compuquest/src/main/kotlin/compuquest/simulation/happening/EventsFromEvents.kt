package compuquest.simulation.happening

import compuquest.simulation.general.deleteBodyCommand
import compuquest.simulation.input.Commands
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

fun eventsFromEvents(events: Events): Events {
  return events
    .filter { it.type == Commands.joinedPlayer && it.value is Id }
    .map { Event(deleteBodyCommand, it.target as Id) }
}
