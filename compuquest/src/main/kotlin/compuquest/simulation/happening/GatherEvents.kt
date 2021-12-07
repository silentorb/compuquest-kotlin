package compuquest.simulation.happening

import compuquest.simulation.combat.eventsFromMissile
import compuquest.simulation.general.World
import compuquest.simulation.intellect.pursueGoals
import silentorb.mythic.happening.Events

fun <K, V> tableEvents(transform: (K, V) -> Events, table: Map<K, V>): Events =
	table.flatMap { (id, record) -> transform(id, record) }

fun gatherEvents(world: World, previous: World?, delta: Float, events: Events): Events {
	val deck = world.deck

	val events2 = deck.spirits.flatMap { pursueGoals(world, it.key) } +
			tableEvents(eventsFromMissile(world, delta), deck.missiles)
//      tableEvents(eventsFromCharacter(world, previous), deck.characters) +
//      tableEvents(eventsFromHomingMissile(world, delta), deck.homingMissiles) +
//      tableEvents(eventsFromFaction(), deck.factions) +
//      eventsFromWares(deck) +
//      eventsFromQuests(deck) +

	return eventsFromEvents(world, previous, events + events2)
}
