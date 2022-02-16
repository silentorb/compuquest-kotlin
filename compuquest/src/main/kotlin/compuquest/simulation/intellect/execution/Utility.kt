package compuquest.simulation.intellect.execution

import compuquest.simulation.general.World
import compuquest.simulation.intellect.design.Goal
import godot.core.Vector3
import silentorb.mythic.ent.Id

fun lookAtTarget(world: World, actor: Id, goal: Goal) {
	val targetEntity = goal.targetEntity
	if (targetEntity != null) {
		val body = world.deck.bodies[actor]
		val targetBody = world.deck.bodies[targetEntity]
		if (body != null && targetBody != null) {
			val target = targetBody.globalTransform.origin
			val flattenedTarget = Vector3(target.x, body.globalTransform.origin.y, target.z)
			body.lookAt(flattenedTarget, Vector3.UP)
		}
	}
}
