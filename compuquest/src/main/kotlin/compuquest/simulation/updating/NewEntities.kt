package compuquest.simulation.updating

import compuquest.simulation.general.*
import compuquest.simulation.happening.Events
import godot.Spatial
import silentorb.mythic.ent.NextId
import silentorb.mythic.ent.Table

inline fun <reified T> extractComponents(nextId: NextId, hands: List<Hand>): Table<T> =
  hands
    .mapNotNull { hand ->
      val component = hand.components.filterIsInstance<T>().firstOrNull()
      if (component != null)
        (hand.id ?: nextId()) to component
      else
        null
    }
    .associate { it }


inline fun <reified T> extractComponents(hands: List<Hand>): Table<T> =
  hands
    .mapNotNull { hand ->
      if (hand.id != null) {
        val component = hand.components.filterIsInstance<T>().firstOrNull()
        if (component != null)
          hand.id to component
        else
          null
      } else
        null
    }
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
  val bodies = extractComponents<Spatial>(idHands)
  if (world.scene != null) {
    for (body in bodies.values) {
      if (body.getParent() == null)
        world.scene.addChild(body)
    }
  }
  return world.copy(
    bodies = world.bodies + bodies,
    deck = deck.copy(
      accessories = deck.accessories + extractComponents(idHands),
      characters = deck.characters + extractComponents(idHands),
      factions = deck.factions + extractComponents(idHands),
      homingMissiles = deck.homingMissiles + extractComponents(idHands),
      players = deck.players + extractComponents(idHands),
      spirits = deck.spirits + extractComponents(idHands),
    )
  )
}

fun newEntities(events: Events, world: World): World {
  val hands = events
    .filter { it.type == newHandCommandKey }
    .mapNotNull { it.value as? Hand }

  return newEntitiesFromHands(hands, world)
}
