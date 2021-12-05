package compuquest.simulation.updating

import compuquest.simulation.characters.updateCharacter
import compuquest.simulation.general.*
import compuquest.simulation.intellect.updateSpirit
import silentorb.mythic.ent.mapTable
import silentorb.mythic.happening.Events

fun updateDeck(events: Events, world: World, delta: Float): Deck {
  val deck = world.deck
  return deck.copy(
    accessories = mapTable(deck.accessories, updateAccessory(events, delta)),
    characters = mapTable(deck.characters, updateCharacter(world, events)),
    factions = mapTable(deck.factions, updateFaction(world, events)),
    players = mapTable(deck.players, updatePlayer(world, events, delta)),
    quests = mapTable(deck.quests, updateQuest(events)),
    spirits = mapTable(deck.spirits, updateSpirit(world)),
    wares = mapTable(deck.wares, updateWare(events)),
  )
}
