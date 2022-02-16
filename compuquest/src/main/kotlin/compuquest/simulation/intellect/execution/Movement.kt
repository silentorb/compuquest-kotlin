package compuquest.simulation.intellect.execution

import compuquest.simulation.general.World
import compuquest.simulation.intellect.navigation.getNavigationAgentVelocity
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

fun moveTowardDestination(world: World, actor: Id, destination: Vector3): Events {
	val body = world.deck.bodies[actor] as? CharacterBody
	if (body != null) {
		val origin = body.globalTransform.origin
		val velocity = (destination - origin)
		velocity.y = 0.0
		val direction = velocity.normalized()
		body.moveDirection = direction
		body.lookAt(origin + direction, Vector3.UP)
	}
	return listOf()
}

fun moveTowardDestination(world: World, actor: Id): Events {
	val body = world.deck.bodies[actor] as? CharacterBody
	if (body != null) {
		val velocity = getNavigationAgentVelocity(world, actor)
		if (velocity != null) {
			val direction = Vector3(velocity.x, 0.0, velocity.z).normalized()
			if (direction != Vector3.ZERO) {
				body.moveDirection = direction
				body.lookAt(body.globalTransform.origin + direction, Vector3.UP)
			}
		}
	}
	return listOf()
}
