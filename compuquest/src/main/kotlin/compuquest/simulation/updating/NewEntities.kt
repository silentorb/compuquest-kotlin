package compuquest.simulation.updating

import compuquest.simulation.general.*
import compuquest.simulation.happening.Events
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
  return world.copy(
    bodies = world.bodies + extractComponents(nextId, hands),
    deck = deck.copy(
      characters = deck.characters + extractComponents(nextId, hands),
      players = deck.players + extractComponents(nextId, hands),
    )
  )
}

fun newEntities(events: Events, world: World): World {
  val hands = events
    .filter { it.type == newHandCommandKey }
    .mapNotNull { it.value as? Hand }

  return newEntitiesFromHands(hands, world)
}
