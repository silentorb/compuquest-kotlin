package compuquest.simulation.updating

import compuquest.simulation.general.*
import silentorb.mythic.ent.mapTable
import silentorb.mythic.happening.Events

fun updateDeck(events: Events, world: World, delta: Float): Deck {
  val deck = world.deck
  return deck.copy(
    accessories = mapTable(deck.accessories, updateAccessory(events, delta)),
    characters = mapTable(deck.characters, updateCharacter(events, world)),
    factions = mapTable(deck.factions, updateFaction(world, events)),
    players = mapTable(deck.players, updatePlayer(world, events)),
  )
}
