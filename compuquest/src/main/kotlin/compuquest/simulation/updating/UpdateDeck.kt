package compuquest.simulation.updating

import compuquest.simulation.general.*
import compuquest.simulation.happening.Events
import silentorb.mythic.ent.mapTable

fun updateDeck(events: Events, world: World): Deck {
  val deck = world.deck
  val definitions = world.definitions
  return deck.copy(
    accessories = mapTable(deck.accessories, updateAccessory(definitions, events)),
    characters = mapTable(deck.characters, updateCharacter(events, world)),
    factions = mapTable(deck.factions, updateFaction(world, events)),
  )
}
