package compuquest.simulation.updating

import compuquest.simulation.general.*
import silentorb.mythic.happening.Events
import silentorb.mythic.ent.mapTable

fun updateDeck(events: Events, world: World, delta: Float): Deck {
  val deck = world.deck
  val definitions = world.definitions
  return deck.copy(
    accessories = mapTable(deck.accessories, updateAccessory(definitions, events, delta)),
    characters = mapTable(deck.characters, updateCharacter(events, world)),
    factions = mapTable(deck.factions, updateFaction(world, events)),
  )
}
