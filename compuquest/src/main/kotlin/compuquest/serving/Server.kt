package compuquest.serving

import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.Player
import compuquest.simulation.general.World
import silentorb.mythic.ent.SharedNextId
import silentorb.mythic.randomly.Dice

fun newWorld(definitions: Definitions): World {
  return World(
    definitions = definitions,
    nextId = SharedNextId(),
    dice = Dice(),
  )
}
