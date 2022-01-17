package compuquest.app

import compuquest.population.processSceneEntities
import compuquest.serving.newWorld
import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.World
import compuquest.simulation.general.getSpace
import compuquest.simulation.updating.updateDepictions
import godot.Spatial

fun newGame(scene: Spatial, definitions: Definitions): World {
  val world = newWorld(definitions, scene)
  val world2 = processSceneEntities(scene, world)
  updateDepictions(null, world2)
  return world2
}
