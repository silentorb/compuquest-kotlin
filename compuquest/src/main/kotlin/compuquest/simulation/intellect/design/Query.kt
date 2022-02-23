package compuquest.simulation.intellect.design

import compuquest.simulation.characters.getAccessoriesSequence
import compuquest.simulation.general.Accessory
import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.Deck
import compuquest.simulation.general.canHeal
import godot.Spatial
import silentorb.mythic.ent.Id
import kotlin.math.abs

typealias AccessoryMapList = List<Map.Entry<Id, Accessory>>

fun getPathDestinations(goal: Goal, body: Spatial?) =
	if (goal.pathDestinations.any() && body != null &&
		body.translation.distanceTo(goal.pathDestinations.first()) < 1f
	)
		goal.pathDestinations.drop(1)
	else
		goal.pathDestinations
