package compuquest.simulation.updating

import compuquest.godoting.tempCatch
import compuquest.simulation.general.Body
import compuquest.simulation.general.World
import compuquest.simulation.happening.Events
import compuquest.simulation.happening.gatherEvents
import godot.core.Vector3

fun syncMythicToGodot(world: World): World {
  val deck = world.deck
  val bodies = world.bodies.mapValues { spatial ->
    Body(
      translation = tempCatch { spatial.value.translation } ?: Vector3.ZERO,
      rotation = tempCatch { spatial.value.rotation } ?: Vector3.ZERO,
    )
  }
  return world.copy(
    deck = deck.copy(
      bodies = bodies,
    )
  )
}

fun updateWorld(events: Events, delta: Float, world: World): World {
  val world2 = syncMythicToGodot(world)
  val events2 = events + gatherEvents(world2, delta)
  val deck = updateDeck(events2, world2)
  val world3 = world2.copy(
    deck = deck
  )
  val world4 = deleteEntities(events2, world3)
  return newEntities(events2, world4)
}
