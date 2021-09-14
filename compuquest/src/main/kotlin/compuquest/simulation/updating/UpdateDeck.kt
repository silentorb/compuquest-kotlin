package compuquest.simulation.updating

import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import compuquest.simulation.general.updateAccessory
import compuquest.simulation.general.updateFaction
import compuquest.simulation.happening.Events
import silentorb.mythic.ent.mapTable

fun updateDeck(events: Events, world: World): Deck {
  val deck = world.deck
  val definitions = world.definitions
  return deck.copy(
    accessories = mapTable(deck.accessories, updateAccessory(definitions, events)),
    factions = mapTable(deck.factions, updateFaction(world, events)),
  )
}
