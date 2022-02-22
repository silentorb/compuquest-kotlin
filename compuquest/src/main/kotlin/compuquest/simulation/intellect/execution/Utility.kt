package compuquest.simulation.intellect.execution

import compuquest.simulation.general.World
import compuquest.simulation.intellect.design.Goal
import compuquest.simulation.physics.getLocation
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId

fun lookAtTarget(world: World, actor: Id, goal: Goal) {
	val targetEntity = goal.targetEntity
	if (targetEntity != emptyId) {
		val body = world.deck.bodies[actor]
		val targetBody = world.deck.bodies[targetEntity]
		if (body != null && targetBody != null) {
			val target = getLocation(targetBody)
			val flattenedTarget = Vector3(target.x, getLocation(body).y, target.z)
			body.lookAt(flattenedTarget, Vector3.UP)
		}
	}
}
