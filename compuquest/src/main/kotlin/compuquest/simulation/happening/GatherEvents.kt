package compuquest.simulation.happening

import compuquest.simulation.characters.eventsFromCharacter
import compuquest.simulation.combat.eventsFromBuffs
import compuquest.simulation.combat.eventsFromMissile
import compuquest.simulation.general.World
import compuquest.simulation.general.eventsFromPlayer
import compuquest.simulation.input.PlayerInputs
import compuquest.simulation.input.gatherPlayerUseActions
import compuquest.simulation.intellect.execution.pursueGoals
import silentorb.mythic.happening.Events

fun <K, V> tableEvents(transform: (K, V) -> Events, table: Map<K, V>): Events =
	table.flatMap { (id, record) -> transform(id, record) }

fun gatherWorldEvents(world: World, previous: World?, playerInputs: PlayerInputs, delta: Float): Events {
	val deck = world.deck

	return deck.spirits.flatMap { pursueGoals(world, it.key) } +
			gatherPlayerUseActions(deck, playerInputs) +
			tableEvents(eventsFromMissile(world, delta), deck.missiles) +
			tableEvents(eventsFromPlayer(world), deck.players) +
			eventsFromBuffs(world) +
			if (previous != null)
				tableEvents(eventsFromCharacter(previous), deck.characters)
			else
				listOf()
}
