package compuquest.simulation.combat

import compuquest.simulation.general.*
import compuquest.simulation.happening.UseAction
import compuquest.simulation.happening.useActionEvent
import godot.Spatial
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import compuquest.simulation.characters.Character
import compuquest.simulation.characters.defaultCharacterHeight
import compuquest.simulation.characters.getCharacterFacing
import compuquest.simulation.happening.TryActionEvent

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
	val accessory = world.deck.accessories[attack.accessory]!!
	accessory.definition.effects.flatMap { effect ->
		when (effect.type) {
			AccessoryEffects.attack -> missileAttack(world, actor, accessory, attack.targetLocation, attack.targetEntity)
			AccessoryEffects.summonAtTarget -> summonAtTarget(world, actor, accessory, attack.targetLocation!!)
			else -> listOf()
		}
	}
}

fun attackOld(world: World, actor: Id, character: Character, action: Id, accessory: Accessory, target: Id): Events {
	throw Error("No longer supported")
//  val definition = accessory.definition
//  val spawns = definition.spawns ?: return listOf()
//  val projectileBody = instantiateScene<Spatial>(spawns)
//  val actorBody = world.deck.bodies[actor]!!
//  projectileBody?.translation = actorBody.translation // + Vector3(0f, -1f, 0f)
//  val projectile = Hand(
//    components = listOfNotNull(
//      HomingMissile(
//        damage = definition.strength.toInt(),
//        target = target,
//        owner = actor,
//        speed = 30f,
//      ),
//      projectileBody,
//    ),
//  )
//
//  return listOf(
//    Event(useActionEvent, actor, action),
//    newHandEvent(projectile),
//  )
}

fun getToolOffset(world: World, actor: Id): Vector3 =
	world.deck.characters[actor]?.toolOffset ?: Vector3.ZERO

fun getAttackerOriginAndFacing(
	world: World, attacker: Id, targetLocation: Vector3?, targetEntity: Id?,
	forwardOffset: Float
): Pair<Vector3, Vector3>? {
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
		getCharacterFacing(world, attacker)
	else
		((target + targetOffset) - baseOrigin).normalized()

	return if (vector == null)
		null
	else {
		val origin = baseOrigin + vector * forwardOffset
		Pair(origin, vector)
	}
}
