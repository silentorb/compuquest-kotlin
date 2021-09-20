package compuquest.simulation.updating

import silentorb.mythic.godoting.deleteNode
import compuquest.simulation.general.World
import compuquest.simulation.general.deleteBodyCommand
import compuquest.simulation.general.deleteEntityCommand
import compuquest.simulation.general.removeEntities
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventTargets
import silentorb.mythic.ent.Id

fun deleteEntities(events: Events, world: World): World {
  val deletions = filterEventTargets<Id>(deleteEntityCommand, events)
  val bodyDeletions = filterEventTargets<Id>(deleteBodyCommand, events)
  val deck = world.deck

  val distinctBodyDeletions = (deletions + bodyDeletions).distinct()
  for (deletion in distinctBodyDeletions) {
    val body = world.bodies[deletion]
    if (body != null) {
      deleteNode(body)
    }
  }
  return world.copy(
    bodies = world.bodies - deletions,
    deck = removeEntities(deletions)(deck),
  )
}
