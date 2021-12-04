package compuquest.simulation.intellect

import compuquest.simulation.combat.attack
import compuquest.simulation.general.*
import godot.PhysicsDirectSpaceState
import godot.core.Vector3
import godot.core.variantArrayOf
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

data class Spirit(
  val actionChanceAccumulator: Int = 0,
  val focusedAction: Id? = null,
  val target: Id? = null,
)

fun inRangeAndVisible(
  space: PhysicsDirectSpaceState,
  world: World,
  bodyId: Id,
  body: Body,
  other: Character,
  range: Float
): Boolean {
  val otherBody = world.bodies[other.body]
  val location = body.translation + Vector3(0f, 1f, 0f)

  return otherBody != null
      && location.distanceTo(otherBody.translation + Vector3(0f, 1f, 0f)) <= range
      && space.intersectRay(body.translation, otherBody.translation, variantArrayOf(world.bodies[bodyId]!!, otherBody))
    .none()
}

fun filterEnemyTargets(
  world: World,
  actor: Id,
  character: Character,
  action: Accessory
): Map<Id, Character> {
  val deck = world.deck
  val bodies = deck.bodies
  val body = bodies[character.body] ?: return mapOf()
  val space = getSpace(world) ?: return mapOf()
  val definition = action.definition
  val range = definition.range
  return deck.characters
    .filter { (id, other) ->
      id != actor
          && other.isAlive
          && isEnemy(world.factionRelationships, other.faction, character.faction)
          && inRangeAndVisible(space, world, character.body!!, body, other, range)
    }
}

fun filterAllyTargets(
  world: World,
  actor: Id,
  character: Character
): Map<Id, Character> {
  val deck = world.deck
  val player = world.deck.players.entries.firstOrNull()
  return if (player != null && character.body == player.key)
    player.value.party
      .minus(actor)
      .associateWith { deck.characters[it]!! }
  else
    mapOf()
}

fun getNextTarget(
  world: World,
  actor: Id,
  accessory: Accessory,
  target: Id?
): Id? {
  val character = world.deck.characters[actor]!!
  val options = filterEnemyTargets(world, actor, character, accessory)
  return if (options.containsKey(target))
    target
  else
    world.dice.takeOneOrNull(options.entries)?.key
}

fun tryUseAction(world: World, actor: Id, character: Character, spirit: Spirit): Events {
  val action = spirit.focusedAction
  val accessory = world.deck.accessories[action]
  return if (action != null && accessory != null) {
    when (accessory.definition.effect) {
      AccessoryEffects.attack -> {
        val target = spirit.target
        if (target != null) {
          attack(world, actor, character, action, accessory, target)
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

fun updateSpirit(world: World): (Id, Spirit) -> Spirit = { actor, spirit ->
  // Ensure the same action is never immediately tried twice in a row.
  // With the current setup, that situation could lead to race conditions.
  val readyActions = getReadyAccessories(world, actor)
    .minus(spirit.focusedAction ?: 0L) // Branching shortcut.  Assuming 0L is never a valid key.

  val focusedAction = world.dice.takeOneOrNull(readyActions.keys)
  val accessory = world.deck.accessories[focusedAction]
  val target = if (accessory != null && accessory.definition.effect == AccessoryEffects.attack)
    getNextTarget(world, actor, accessory, spirit.target)
  else
    spirit.target

  spirit.copy(
    focusedAction = focusedAction,
    target = target,
  )
}
