package compuquest.simulation.happening

import compuquest.simulation.combat.eventsFromHomingMissile
import compuquest.simulation.general.World
import compuquest.simulation.intellect.updateSpirit
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table

fun <T> tableEvents(transform: (Id, T) -> Events, table: Table<T>): Events =
  table.flatMap { (id, record) -> transform(id, record) }

fun gatherEvents(world: World, delta: Float): Events {
  val deck = world.deck
  val commands = deck.spirits.flatMap { updateSpirit(world, it.key) } +
      tableEvents(eventsFromHomingMissile(world, delta), deck.homingMissiles)

  return commands
}
