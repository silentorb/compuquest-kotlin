package compuquest.simulation.intellect.execution

import compuquest.simulation.characters.Character
import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.World
import compuquest.simulation.happening.TryActionEvent
import compuquest.simulation.happening.tryActionEvent
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.design.Goal
import compuquest.simulation.intellect.navigation.getNavigationAgentVelocity
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

fun lookAtTarget(world: World, actor: Id, goal: Goal) {
	val targetEntity = goal.targetEntity
	if (targetEntity != null) {
		val body = world.bodies[actor]
		val targetBody = world.bodies[targetEntity]
		if (body != null && targetBody != null) {
			val target = targetBody.globalTransform.origin
			val flattenedTarget = Vector3(target.x, body.globalTransform.origin.y, target.z)
			body.lookAt(flattenedTarget, Vector3.UP)
		}
	}
}

fun tryUseAction(world: World, actor: Id, character: Character, spirit: Spirit): Events {
	val goal = spirit.goal
	val action = goal.focusedAction
	val accessory = world.deck.accessories[action]
	val effects = accessory?.definition?.actionEffects ?: listOf()
	lookAtTarget(world, actor, goal)
	
	return if (action != null)
		effects.flatMap { effect ->
			when (effect.type) {
				AccessoryEffects.damage -> {
					val target = goal.targetEntity
					if (target != null) {
						listOf(newEvent(tryActionEvent, actor, TryActionEvent(action = action, targetEntity = target)))
					} else
						listOf()
				}
				AccessoryEffects.summonAtTarget -> {
					val target = goal.targetEntity
					if (target != null) {
						val body = world.bodies[target]!!
						val targetLocation = body.translation //+ offset
						val value = TryActionEvent(
							action = action,
							targetLocation = targetLocation,
							targetEntity = target, // Not needed for the summoning but used to trigger user targeting effects
						)
						listOf(newEvent(tryActionEvent, actor, value))
					} else
						listOf()
				}
//      AccessoryEffects.heal -> {
//        val strength = definition.strengthInt
//        val targets = filterAllyTargets(world, actor, character)
//          .filterValues {
//            it.isAlive &&
//                (it.health.value <= it.health.max - strength || it.health.value < it.health.max / 2)
//          }
//
//        val target = world.dice.takeOneOrNull(targets.keys)
//        if (target != null) {
//          heal(action, accessory, target)
//        } else
//          listOf()
//      }
				else -> listOf()
			}
		} else
		listOf()
}

fun moveTowardDestination(world: World, actor: Id, destination: Vector3): Events {
	val body = world.bodies[actor] as? CharacterBody
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
	val body = world.bodies[actor] as? CharacterBody
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

fun pursueGoals(world: World, actor: Id): Events {
	val deck = world.deck
	val character = deck.characters[actor]!!
	val spirit = world.deck.spirits[actor]
	return if (character.isAlive && spirit != null) {
		val goal = spirit.goal
		when {
			goal.destination != null -> moveTowardDestination(world, actor)
			goal.readyToUseAction -> tryUseAction(world, actor, character, spirit)
			else -> listOf()
		}
	} else
		listOf()
}
