package compuquest.app

import compuquest.population.MaterialMap
import compuquest.population.processSceneEntities
import compuquest.serving.newWorld
import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.Scenario
import compuquest.simulation.general.World
import compuquest.simulation.general.getSpace
import compuquest.simulation.updating.updateDepictions
import godot.Spatial

fun newGame(scene: Spatial, scenario: Scenario, definitions: Definitions, materials: MaterialMap): World {
  val world = newWorld(definitions, scenario, scene)
  val world2 = processSceneEntities(scene, world, materials)
  updateDepictions(null, world2.deck)
  return world2
}
