package compuquest.app

import compuquest.serving.newWorld
import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.processSceneEntities
import godot.Node

fun newAppState(scene: Node, definitions: Definitions): AppState {
  val world = newWorld(definitions)
  val world2 = processSceneEntities(scene, world)
  return AppState(
    world = world2,
  )
}
