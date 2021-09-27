package compuquest.app

import compuquest.population.populateQuests
import compuquest.serving.newWorld
import compuquest.serving.populateZones
import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.World
import compuquest.simulation.general.processSceneEntities
import godot.Node
import silentorb.mythic.randomly.Dice

fun newGame(scene: Node, definitions: Definitions): World {
  val world = newWorld(definitions)
    .copy(scene = scene)

  populateZones(Dice(), scene)

  val world2 = processSceneEntities(scene, world)

  return populateQuests(world2)
}
