package compuquest.serving

import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.*
import compuquest.simulation.intellect.navigation.NavigationState
import compuquest.simulation.intellect.navigation.generateNavMeshInputVisualization
import compuquest.simulation.intellect.navigation.generateNavMeshVisualization
import compuquest.simulation.intellect.navigation.newNavigationState
import godot.Node
import godot.Spatial
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.debugging.getDebugInt
import silentorb.mythic.ent.SharedNextId
import silentorb.mythic.randomly.Dice

fun newWorldNavigation(scene: Node): NavigationState? {
	val navigation = newNavigationState(scene)
	if (getDebugBoolean("DISPLAY_NAVMESH_INPUT")) {
		scene.addChild(generateNavMeshInputVisualization(scene))
	}
	if (getDebugBoolean("DISPLAY_NAVMESH") && navigation != null) {
		scene.addChild(generateNavMeshVisualization(navigation.mesh))
	}

	return navigation
}

fun newWorld(definitions: Definitions, scenario: Scenario, scene: Spatial): World {
	val space = getSpace(scene)!!
	return World(
		definitions = definitions,
		scenario = scenario,
		nextId = SharedNextId(),
		dice = Dice(),
		day = DayState(dayLength = getDebugInt("DAY_LENGTH") ?: 5 * dayMinutes),
		scene = scene,
		space = space,
	)
}
