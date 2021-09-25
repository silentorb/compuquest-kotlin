package compuquest.simulation.input

import godot.Input
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

val keyStrokes = setOf(
  Commands.interact,
  Commands.managementMenu,
  Commands.menuBack,
)

fun gatherUserInput(player: Id): Events =
  keyStrokes.filter { Input.isActionJustReleased(it) }
    .map { Event(it, player) }
