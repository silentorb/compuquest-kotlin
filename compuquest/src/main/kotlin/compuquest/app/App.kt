package compuquest.app

import compuquest.serving.newWorld
import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.World
import compuquest.simulation.general.processSceneEntities
import godot.Node

fun newAppState(scene: Node, definitions: Definitions): World {
  val world = newWorld(definitions)
    .copy(scene = scene)

  return processSceneEntities(scene, world)
}
