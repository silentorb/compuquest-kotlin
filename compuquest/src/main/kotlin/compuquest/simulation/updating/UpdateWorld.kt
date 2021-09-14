package compuquest.simulation.updating

import compuquest.simulation.general.World
import compuquest.simulation.happening.Events
import compuquest.simulation.happening.generateEvents

fun updateWorld(events: Events, world: World): World {
  val commands2 = events + generateEvents(world)
  val deck = updateDeck(commands2, world)
  val world2 = world.copy(
    deck = deck
  )
  return newEntities(commands2, world2)
}
