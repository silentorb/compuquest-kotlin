package compuquest.simulation.updating

import compuquest.simulation.characters.updateCharacter
import compuquest.simulation.general.*
import compuquest.simulation.intellect.navigation.NavigationState
import compuquest.simulation.intellect.navigation.updateNavigationDirections
import silentorb.mythic.ent.mapTable
import silentorb.mythic.ent.mapTableValues
import silentorb.mythic.happening.Events
import silentorb.mythic.timing.updateTimer

fun updateDeck(events: Events, world: World, navigation: NavigationState?, delta: Float): Deck {
	val deck = world.deck
	return deck.copy(
		accessories = mapTable(deck.accessories, updateAccessory(events, delta)),
		characters = mapTable(deck.characters, updateCharacter(world, events)),
		factions = mapTable(deck.factions, updateFaction(world, events)),
//    missiles = mapTable(deck.missiles, updateMissile(world, events)),
//		navigationDirections = if (navigation != null) updateNavigationDirections(navigation) else mapOf(),
		players = mapTable(deck.players, updatePlayer(world, events, delta)),
		quests = mapTable(deck.quests, updateQuest(events)),
		timers = mapTableValues(deck.timers, updateTimer),
		wares = mapTable(deck.wares, updateWare(events)),
	)
}
