package compuquest.simulation.happening

import compuquest.simulation.general.World
import compuquest.simulation.intellect.updateSpirit

fun generateEvents(world: World): Events {
  val deck = world.deck
  val commands = deck
    .spirits.flatMap { updateSpirit(world, it.key) }

  return commands
}
