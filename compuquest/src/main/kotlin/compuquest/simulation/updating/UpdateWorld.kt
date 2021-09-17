package compuquest.simulation.updating

import compuquest.godoting.tempCatch
import compuquest.simulation.general.Body
import compuquest.simulation.general.World
import silentorb.mythic.happening.Events
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

fun updateDepictions(previous: World, next: World) {
  for (after in next.deck.characters) {
    val before = previous.deck.characters[after.key]
    val animation = after.value.depiction
    if (before?.depiction != animation) {
      val node = next.sprites[after.key]
      if (node != null) {
        tempCatch { node.set("animation", animation) }
      }
    }
  }
}

fun updateWorld(events: Events, delta: Float, worlds: List<World>): World {
  val world = worlds.last()
  val world2 = syncMythicToGodot(world)
  val events2 = events + gatherEvents(world2, worlds.dropLast(1).firstOrNull(), delta)
  val deck = updateDeck(events2, world2, delta)
  val world3 = world2.copy(
    deck = deck
  )
  updateDepictions(world, world3)
  val world4 = deleteEntities(events2, world3)
  return newEntities(events2, world4)
}
