package compuquest.simulation.updating

import compuquest.simulation.characters.updateOwnedAccessory
import compuquest.simulation.characters.updateCharacter
import compuquest.simulation.characters.updateContainers
import compuquest.simulation.general.*
import compuquest.simulation.input.PlayerInputs
import silentorb.mythic.ent.mapTable
import silentorb.mythic.ent.mapTableValues
import silentorb.mythic.happening.Events
import silentorb.mythic.timing.updateTimer

fun updateDeck(events: Events, world: World, inputs: PlayerInputs, delta: Float): Deck {
	val deck = world.deck
	return deck.copy(
		characters = mapTable(deck.characters, updateCharacter(world, inputs, events)),
		containers = mapTable(deck.containers, updateContainers(world, events)),
		players = mapTable(deck.players, updatePlayer(world, events, delta)),
		quests = mapTable(deck.quests, updateQuest(events)),
		timers = mapTableValues(deck.timers, updateTimer),
		wares = mapTable(deck.wares, updateWare(events)),
	)
}
