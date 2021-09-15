package compuquest.simulation.updating

import compuquest.godoting.deleteNode
import compuquest.simulation.general.World
import compuquest.simulation.general.deleteEntityCommand
import compuquest.simulation.general.removeEntities
import compuquest.simulation.happening.Events
import compuquest.simulation.happening.filterEventTargets
import silentorb.mythic.ent.Id

fun deleteEntities(events: Events, world: World): World {
  val deletions = filterEventTargets<Id>(deleteEntityCommand, events)
  val deck = world.deck

  for (deletion in deletions) {
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
