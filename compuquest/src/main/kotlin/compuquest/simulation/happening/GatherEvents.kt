package compuquest.simulation.happening

import compuquest.simulation.combat.eventsFromHomingMissile
import compuquest.simulation.general.World
import compuquest.simulation.input.gatherUserInput
import compuquest.simulation.intellect.pursueGoals
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.happening.Events

fun <T> tableEvents(transform: (Id, T) -> Events, table: Table<T>): Events =
  table.flatMap { (id, record) -> transform(id, record) }

fun gatherEvents(world: World, previous: World?, delta: Float, events: Events): Events {
  val deck = world.deck
  val player = world.deck.players.keys.firstOrNull()
  val input = if (player != null)
    gatherUserInput(player)
  else
    listOf()

  val commands = input +
      deck.spirits.flatMap { pursueGoals(world, it.key) } +
//      tableEvents(eventsFromCharacter(world, previous), deck.characters) +
      tableEvents(eventsFromHomingMissile(world, delta), deck.homingMissiles) +
      eventsFromEvents(events)

  return commands
}
