package compuquest.serving

import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.Factions
import compuquest.simulation.general.*
import godot.PhysicsDirectSpaceState
import godot.Spatial
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.debugging.getDebugInt
import silentorb.mythic.ent.SharedNextId
import silentorb.mythic.randomly.Dice

fun newWorld(definitions: Definitions, scenario: Scenario, scene: Spatial): World {
	val space = getSpace(scene)!!
	return World(
		definitions = definitions,
		scenario = scenario,
		nextId = SharedNextId(),
		dice = Dice(),
		factionRelationships = mapOf(
			setOf(Factions.undead, Factions.player) to 10,
		),
		day = DayState(dayLength = getDebugInt("DAY_LENGTH") ?: 5 * dayMinutes),
		scene = scene,
		space = space,
	)
}
