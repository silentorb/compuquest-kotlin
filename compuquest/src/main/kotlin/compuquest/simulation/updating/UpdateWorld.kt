package compuquest.simulation.updating

import compuquest.simulation.general.*
import silentorb.mythic.godoting.tempCatch
import silentorb.mythic.happening.Events
import compuquest.simulation.happening.gatherEvents
import godot.core.Vector3
import scripts.Global

fun syncMythic(world: World): World {
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

fun syncGodot(world: World, events: Events) {
  val player = getPlayer(world)
  if (player != null) {
    val playerRigIsActive = player.value.interactingWith == null && Global.getMenuStack().none() && player.value.isPlaying
    val body = world.bodies[player.key]
    if (body != null) {
      body.set("isActive", playerRigIsActive)
      if (shouldRefreshPlayerSlowdown(player.value, events)) {
        body.set("isSlowed", true)
      }
    }
  }
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

fun updateWorldDay(world: World): World =
  world.copy(
    day = updateDay(world.day),
    step = world.step + 1
  )

fun updateWorld(events: Events, delta: Float, worlds: List<World>): World {
  val previousWorld = worlds.last()
  val world = updateWorldDay(previousWorld)

  val world2 = syncMythic(world)
  val events2 = gatherEvents(world2, previousWorld, delta, events)
  val deck = updateDeck(events2, world2, delta)
  val world3 = world2.copy(
    deck = deck,
  )
  updateDepictions(world, world3)
  val world4 = deleteEntities(events2, world3)
  val world5 = newEntities(events2, world4)
  syncGodot(world5, events2)
  return world5
}
