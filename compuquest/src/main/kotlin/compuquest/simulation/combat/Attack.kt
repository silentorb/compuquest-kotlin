package compuquest.simulation.combat

import compuquest.simulation.characters.*
import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import compuquest.simulation.happening.UseAction
import godot.core.Transform
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

const val attackEvent = "attackEvent"

data class Attack(
	val accessory: Id,
	val targetLocation: Vector3? = null,
	val targetEntity: Id? = null, // Only used for attacks that are not spatial, which is rare
)

fun startAttack(
	action: Id, accessoryRecord: Accessory, attacker: Id, targetLocation: Vector3?, targetEntity: Id?
): Event {
	val attack = Event(
		type = attackEvent,
		target = attacker,
		value = Attack(
			accessory = action,
			targetLocation = targetLocation,
			targetEntity = targetEntity,
		)
	)

	return if (accessoryRecord.definition.animation == null)
		attack
	else // TODO: This code is not fully implemented yet
		Event(
			type = "not yet defined",
			target = attacker,
			value = UseAction(
				action = action,
				deferredEvents = mapOf(
//          executeMarker to attackEvent
				)
			)
		)
}

fun eventsFromAttacks(world: World): (Id, Attack) -> Events = { actor, attack ->
	val deck = world.deck
	val accessory = getOwnerAccessory(deck, actor, attack.accessory)!!
	accessory.definition.actionEffects
		.flatMap { effect ->
			when (effect.type) {
				AccessoryEffects.damage -> missileAttack(world, actor, accessory, attack.targetLocation)
				AccessoryEffects.summonAtTarget -> forEachSummonEffect(accessory.definition) {
					summonAtLocation(world, actor, it, Transform().translated(attack.targetLocation!!))
				}
				else -> listOf()
			}
		}
}

fun getToolTransform(deck: Deck, actor: Id): Transform? =
	(deck.bodies[actor] as? CharacterBody)?.getToolTransform()

fun getTargetLocation(deck: Deck, attack: Attack): Vector3? =
	attack.targetLocation ?: if (attack.targetEntity != null)
		(deck.bodies[attack.targetEntity] as? CharacterBody)?.getToolTransform()?.origin
	else
		null

fun applyEffectTransform(effect: AccessoryEffect, transform: Transform): Transform =
	if (effect.transform != null)
		transform * effect.transform
	else
		transform

fun getAttackerOriginAndFacing(deck: Deck, actor: Id, targetLocation: Vector3?): Transform {
	val toolTransform = getToolTransform(deck, actor)!!

	return if (targetLocation == null)
		toolTransform
	else
		toolTransform.lookingAt(targetLocation, Vector3.UP)
}
