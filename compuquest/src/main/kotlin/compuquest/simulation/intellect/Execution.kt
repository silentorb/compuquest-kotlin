package compuquest.simulation.intellect

import compuquest.simulation.characters.Character
import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.World
import compuquest.simulation.happening.TryActionEvent
import compuquest.simulation.happening.tryActionEvent
import compuquest.simulation.updating.simulationDelta
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

fun tryUseAction(world: World, actor: Id, character: Character, spirit: Spirit): Events {
	val action = spirit.focusedAction
	val accessory = world.deck.accessories[action]
	val effect = accessory?.definition?.actionEffects?.firstOrNull()
	return if (effect != null && action != null) {
		when (effect.type) {
			AccessoryEffects.attack -> {
				val target = spirit.target
				if (target != null) {

					listOf(newEvent(tryActionEvent, actor, TryActionEvent(action = action, targetEntity = target)))
				} else
					listOf()
			}
			AccessoryEffects.summonAtTarget -> {
				val target = spirit.target
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
		val velocity = (destination - body.globalTransform.origin)
		velocity.y = 0.0
		body.moveDirection = velocity.normalized()
	}
	return listOf()
}

fun pursueGoals(world: World, actor: Id): Events {
	val deck = world.deck
	val character = deck.characters[actor]!!
	val spirit = world.deck.spirits[actor]
	return if (character.isAlive && spirit != null)
		when {
			spirit.nextDestination != null -> moveTowardDestination(world, actor, spirit.nextDestination)
			else -> tryUseAction(world, actor, character, spirit)
		}
	else
		listOf()
}
