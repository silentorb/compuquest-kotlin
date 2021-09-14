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

fun newEntitiesFromHands(hands: List<Hand>, world: World): World {
  val nextId = world.nextId.source()
  val deck = world.deck
  val bodies = extractComponents<Spatial>(nextId, hands)
  if (world.scene != null) {
    for (body in bodies.values) {
      if (body.getParent() == null)
        world.scene.addChild(body)
    }
  }
  return world.copy(
    bodies = world.bodies + bodies,
    deck = deck.copy(
      accessories = deck.accessories + extractComponents(nextId, hands),
      characters = deck.characters + extractComponents(nextId, hands),
      factions = deck.factions + extractComponents(nextId, hands),
      missiles = deck.missiles + extractComponents(nextId, hands),
      players = deck.players + extractComponents(nextId, hands),
      spirits = deck.spirits + extractComponents(nextId, hands),
    )
  )
}

fun newEntities(events: Events, world: World): World {
  val hands = events
    .filter { it.type == newHandCommandKey }
    .mapNotNull { it.value as? Hand }

  return newEntitiesFromHands(hands, world)
}
