package compuquest.clienting.input

import compuquest.simulation.general.World
import compuquest.simulation.general.getPlayer
import compuquest.simulation.input.Commands
import godot.Input
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

val keyStrokes = setOf(
  Commands.interact,
  Commands.manageMembers,
  Commands.manageQuests,
  Commands.menuBack,
  Commands.newGame,
)

fun gatherUserInput(player: Id): Events =
  keyStrokes.filter { Input.isActionJustReleased(it) }
    .map { newEvent(it, player) }

fun gatherDefaultPlayerInput(world: World): Events {
  val player = getPlayer(world)?.key
  return if (player != null)
    gatherUserInput(player)
  else
    listOf()
}
