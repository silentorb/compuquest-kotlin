package compuquest.simulation.intellect.design

import compuquest.simulation.characters.Accessory
import godot.Spatial
import silentorb.mythic.ent.Id

typealias AccessoryMapList = List<Map.Entry<Id, Accessory>>

fun getPathDestinations(goal: Goal, body: Spatial?) =
	if (goal.pathDestinations.any() && body != null &&
		body.translation.distanceTo(goal.pathDestinations.first()) < 1f
	)
		goal.pathDestinations.drop(1)
	else
		goal.pathDestinations
