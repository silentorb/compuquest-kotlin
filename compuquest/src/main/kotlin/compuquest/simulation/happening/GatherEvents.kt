package compuquest.simulation.happening

import compuquest.simulation.combat.eventsFromHomingMissile
import compuquest.simulation.general.World
import compuquest.simulation.general.eventsFromFaction
import compuquest.simulation.input.gatherUserInput
import compuquest.simulation.intellect.pursueGoals
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.happening.Events

fun <K, V> tableEvents(transform: (K, V) -> Events, table: Map<K, V>): Events =
  table.flatMap { (id, record) -> transform(id, record) }

fun gatherEvents(world: World, previous: World?, delta: Float, events: Events): Events {
  val deck = world.deck
  val player = world.deck.players.keys.firstOrNull()
  val input = if (player != null)
    gatherUserInput(player)
  else
    listOf()

  val nextEvents = input +
      deck.spirits.flatMap { pursueGoals(world, it.key) } +
//      tableEvents(eventsFromCharacter(world, previous), deck.characters) +
      tableEvents(eventsFromHomingMissile(world, delta), deck.homingMissiles) +
      tableEvents(eventsFromFaction(), deck.factions) +
      eventsFromEvents(world, previous, events)

  return nextEvents
}
