package compuquest.simulation.happening

import compuquest.simulation.combat.eventsFromHomingMissile
import compuquest.simulation.general.World
import compuquest.simulation.general.eventsFromFaction
import compuquest.simulation.general.eventsFromQuests
import compuquest.simulation.general.eventsFromWares
import compuquest.simulation.intellect.pursueGoals
import silentorb.mythic.happening.Events

fun <K, V> tableEvents(transform: (K, V) -> Events, table: Map<K, V>): Events =
  table.flatMap { (id, record) -> transform(id, record) }

fun gatherEvents(world: World, previous: World?, delta: Float, events: Events): Events {
  val deck = world.deck

  val nextEvents = deck.spirits.flatMap { pursueGoals(world, it.key) } +
//      tableEvents(eventsFromCharacter(world, previous), deck.characters) +
      tableEvents(eventsFromHomingMissile(world, delta), deck.homingMissiles) +
      tableEvents(eventsFromFaction(), deck.factions) +
      eventsFromWares(deck) +
      eventsFromQuests(deck) +
      eventsFromEvents(world, previous, events)

  return nextEvents
}
