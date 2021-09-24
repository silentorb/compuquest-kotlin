package compuquest.serving

import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.Factions
import compuquest.simulation.general.DayState
import compuquest.simulation.general.World
import compuquest.simulation.general.dayMinutes
import silentorb.mythic.debugging.getDebugInt
import silentorb.mythic.ent.SharedNextId
import silentorb.mythic.randomly.Dice

fun newWorld(definitions: Definitions): World {
  return World(
    definitions = definitions,
    nextId = SharedNextId(),
    dice = Dice(),
    factionRelationships = mapOf(
      setOf(Factions.undead, Factions.player) to 10,
    ),
    day = DayState(dayLength = getDebugInt("DAY_LENGTH") ?: 5 * dayMinutes)
  )
}
