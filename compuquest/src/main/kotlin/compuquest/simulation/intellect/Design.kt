package compuquest.simulation.intellect

import compuquest.simulation.characters.Character
import compuquest.simulation.characters.getReadyAccessories
import compuquest.simulation.general.*
import godot.PhysicsDirectSpaceState
import godot.core.Vector3
import godot.core.variantArrayOf
import silentorb.mythic.ent.Id

fun inRangeAndVisible(
	space: PhysicsDirectSpaceState,
	world: World,
	bodyId: Id,
	body: Body,
	other: Id,
	range: Float
): Boolean {
	val otherBody = world.bodies[other]
	val location = body.translation + Vector3(0f, 1f, 0f)

	return otherBody != null
			&& location.distanceTo(otherBody.translation + Vector3(0f, 1f, 0f)) <= range
			&& space.intersectRay(body.translation, otherBody.translation, variantArrayOf(world.bodies[bodyId]!!, otherBody))
		.none()
}

fun getTargetRange(
	world: World,
	body: Body,
	other: Id
): Float {
	val otherBody = world.bodies[other]!!
	val location = body.translation + Vector3(0f, 1f, 0f)
	return location.distanceTo(otherBody.translation + Vector3(0f, 1f, 0f)).toFloat()
}

fun isVisible(
	space: PhysicsDirectSpaceState,
	world: World,
	bodyId: Id,
	body: Body,
	other: Id
): Boolean {
	val otherBody = world.bodies[other]
	return otherBody != null &&
			space.intersectRay(body.translation, otherBody.translation, variantArrayOf(world.bodies[bodyId]!!, otherBody))
				.none()
}

fun filterEnemyTargets(
	world: World,
	actor: Id,
	character: Character
): Map<Id, Character> {
	val deck = world.deck
	val bodies = deck.bodies
	val body = bodies[actor] ?: return mapOf()
	val space = getSpace(world) ?: return mapOf()
	return deck.characters
		.filter { (id, other) ->
			id != actor
					&& other.isAlive
					&& isEnemy(world.factionRelationships, other.faction, character.faction)
					&& isVisible(space, world, actor, body, id)
		}
}

fun getNextTarget(
	world: World,
	actor: Id,
	accessory: Accessory,
	target: Id?
): Id? {
	val character = world.deck.characters[actor]!!
	val options = filterEnemyTargets(world, actor, character)
	return if (options.containsKey(target))
		target
	else {
		if (options.entries.size < 2)
			options.keys.firstOrNull()
		else {
			val deck = world.deck
			val bodies = deck.bodies
			val body = bodies[actor]!!
			options.keys
				.map { it to getTargetRange(world, body, it) }
				.minByOrNull { it.second }!!.first
		}
	}
}

fun updateSpirit(world: World): (Id, Spirit) -> Spirit = { actor, spirit ->
	// Ensure the same action is never immediately tried twice in a row.
	// With the current setup, that situation could lead to race conditions.
	val readyActions = getReadyAccessories(world, actor)
		.minus(spirit.focusedAction ?: 0L) // Branching shortcut.  Assuming 0L is never a valid key.

	val focusedAction = world.dice.takeOneOrNull(readyActions.keys)
	val accessory = world.deck.accessories[focusedAction]
	val target = if (accessory != null && accessory.definition.effects.firstOrNull()?.type == AccessoryEffects.attack)
		getNextTarget(world, actor, accessory, spirit.target)
	else
		spirit.target

	spirit.copy(
		focusedAction = focusedAction,
		target = target,
	)
}
