package compuquest.simulation.updating

import compuquest.godoting.tempCatch
import compuquest.simulation.general.*
import silentorb.mythic.happening.Events
import godot.Spatial
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table

inline fun <reified T> extractComponentsRaw(hands: List<Hand>): List<Pair<Id, T>> =
  hands
    .flatMap { hand ->
      if (hand.id != null)
        hand.components
          .filterIsInstance<T>()
          .map { hand.id to it }
      else
        listOf()
    }

inline fun <reified T> extractComponents(hands: List<Hand>): Table<T> =
  extractComponentsRaw<T>(hands)
    .associate { it }

fun newEntitiesFromHands(hands: List<Hand>, world: World): World {
  val nextId = world.nextId.source()
  val deck = world.deck
  val idHands = hands.map { hand ->
    if (hand.id == null)
      hand.copy(id = nextId())
    else
      hand
  }
  val nodes = extractComponentsRaw<Spatial>(idHands)
  val (spritesList, bodiesList) = nodes.partition { it.second.name == "sprite" }
  val sprites = spritesList.associate { it }
  val bodies = bodiesList.associate { it }

  tempCatch {
    if (world.scene != null) {
      for (body in bodies.values) {
        if (body.getParent() == null)
          world.scene.addChild(body)
      }
    }
  }

  return world.copy(
    bodies = world.bodies + bodies,
    sprites = world.sprites + sprites,
    deck = allHandsToDeck(idHands, deck),
  )
}

fun newEntities(events: Events, world: World): World {
  val hands = events
    .filter { it.type == newHandCommandKey }
    .mapNotNull { it.value as? Hand }

  return newEntitiesFromHands(hands, world)
}
