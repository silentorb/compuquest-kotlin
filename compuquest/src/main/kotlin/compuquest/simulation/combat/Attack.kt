package compuquest.simulation.combat

import compuquest.simulation.characters.Accessory
import compuquest.simulation.characters.AccessoryEffects
import compuquest.simulation.characters.getCharacterFacing
import compuquest.simulation.characters.getOwnerAccessory
import compuquest.simulation.general.World
import compuquest.simulation.happening.UseAction
import godot.core.Transform
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

const val attackEvent = "attackEvent"

data class Attack(
	val accessory: Id,
	val targetLocation: Vector3? = null,
	val targetEntity: Id? = null,
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
				AccessoryEffects.damage -> missileAttack(world, actor, accessory, attack.targetLocation, attack.targetEntity)
				AccessoryEffects.summonAtTarget -> forEachSummonEffect(accessory.definition) {
					summonAtLocation(world, actor, it, Transform().translated(attack.targetLocation!!))
				}
				else -> listOf()
			}
		}
}

fun getToolOffset(world: World, actor: Id): Vector3 =
	world.deck.characters[actor]?.toolOffset ?: Vector3.ZERO

fun getAttackerOriginAndFacing(
	world: World, attacker: Id, targetLocation: Vector3?, targetEntity: Id?,
	forwardOffset: Float
): Pair<Vector3?, Vector3> {
	val deck = world.deck
	val body = deck.bodies[attacker]!!
	val toolOffset = getToolOffset(world, attacker)
	val targetOffset = if (targetEntity != null)
		getToolOffset(world, targetEntity)
	else
		Vector3.ZERO

	val target = deck.bodies[targetEntity]?.translation
		?: targetLocation
//		?: null // deck.bodies[deck.targets[attacker]]?.translation

	val baseOrigin = body.translation + toolOffset

	val vector = if (target == null)
		getCharacterFacing(deck, attacker)
	else
		((target + targetOffset) - baseOrigin).normalized()

	return if (vector == null)
		null to Vector3.ZERO
	else {
		val origin = baseOrigin + vector * forwardOffset
		Pair(origin, vector)
	}
}
