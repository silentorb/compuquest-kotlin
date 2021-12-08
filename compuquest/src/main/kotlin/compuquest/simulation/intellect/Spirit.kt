package compuquest.simulation.intellect

import compuquest.simulation.general.*
import godot.PhysicsDirectSpaceState
import godot.core.Vector3
import godot.core.variantArrayOf
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events
import compuquest.simulation.characters.Character
import compuquest.simulation.characters.getReadyAccessories
import compuquest.simulation.happening.TryActionEvent
import compuquest.simulation.happening.tryActionEvent
import silentorb.mythic.happening.newEvent

data class Spirit(
	val actionChanceAccumulator: Int = 0,
	val focusedAction: Id? = null,
	val target: Id? = null,
)

fun tryUseAction(world: World, actor: Id, character: Character, spirit: Spirit): Events {
	val action = spirit.focusedAction
	val accessory = world.deck.accessories[action]
	val effect = accessory?.definition?.effects?.firstOrNull()
	return if (effect != null && action != null) {
		when (effect.type) {
			AccessoryEffects.attack -> {
				val target = spirit.target
				if (target != null) {

					listOf(newEvent(tryActionEvent, actor, TryActionEvent(action = action, targetEntity = target)))
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

fun pursueGoals(world: World, actor: Id): Events {
	val deck = world.deck
	val character = deck.characters[actor]!!
	val spirit = world.deck.spirits[actor]
	return if (character.isAlive && spirit != null)
		tryUseAction(world, actor, character, spirit)
	else
		listOf()
}
