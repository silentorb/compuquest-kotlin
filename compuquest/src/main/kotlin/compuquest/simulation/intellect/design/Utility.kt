package compuquest.simulation.intellect.design

import compuquest.simulation.characters.getReadyAccessories
import compuquest.simulation.general.Accessory
import compuquest.simulation.general.World
import godot.core.Vector3
import silentorb.mythic.ent.Id

fun updateDestination(world: World, actor: Id, targetLocation: Vector3?): Vector3? {
	val navigation = world.navigation
	val sourceLocation = world.deck.bodies[actor]?.translation
	if (navigation == null) {
		println("Current scene is not configured for pathfinding!")
	}
	return if (navigation != null && sourceLocation != null && targetLocation != null) {
		throw Error("Need to hook up new navigation system to updateDestination")
//		val path = navigation.getSimplePath(sourceLocation, targetLocation)
//		path.drop(1).firstOrNull { it.distanceTo(sourceLocation) > 0.1f }
	} else
		null
}

fun updateFocusedAction(world: World, actor: Id): Map.Entry<Id, Accessory>? {
	val readyActions = getReadyAccessories(world, actor)
	return readyActions.maxByOrNull { it.value.definition.range }
//world.dice.takeOneOrNull(readyActions.entries)
}

fun updateUseActionGoal(goal: Goal, accessory: Id): Goal {
	return goal.copy(
		focusedAction = accessory,
		readyTo = ReadyMode.action,
		destination = null,
	)
}
