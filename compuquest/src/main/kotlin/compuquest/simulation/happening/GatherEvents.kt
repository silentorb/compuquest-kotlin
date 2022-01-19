package compuquest.simulation.happening

import compuquest.clienting.input.gatherPlayerUseActions
import compuquest.simulation.characters.eventsFromCharacter
import compuquest.simulation.combat.eventsFromBuffs
import compuquest.simulation.combat.eventsFromMissile
import compuquest.simulation.general.World
import compuquest.simulation.general.eventsFromPlayer
import compuquest.simulation.input.PlayerInputs
import compuquest.simulation.intellect.pursueGoals
import silentorb.mythic.happening.Events

fun <K, V> tableEvents(transform: (K, V) -> Events, table: Map<K, V>): Events =
	table.flatMap { (id, record) -> transform(id, record) }

fun gatherEvents(world: World, previous: World?, playerInputs: PlayerInputs, delta: Float, events: Events): Events {
	val deck = world.deck

	val events2 = deck.spirits.flatMap { pursueGoals(world, it.key) } +
			gatherPlayerUseActions(world.deck, playerInputs) +
			tableEvents(eventsFromMissile(world, delta), deck.missiles) +
			tableEvents(eventsFromPlayer(world), deck.players) +
			eventsFromBuffs(world) +
			if (previous != null)
				tableEvents(eventsFromCharacter(previous), deck.characters)
			else
				listOf()
//      tableEvents(eventsFromHomingMissile(world, delta), deck.homingMissiles) +
//      tableEvents(eventsFromFaction(), deck.factions) +
//      eventsFromWares(deck) +
//      eventsFromQuests(deck) +

	return eventsFromEvents(world, previous, events + events2)
}
