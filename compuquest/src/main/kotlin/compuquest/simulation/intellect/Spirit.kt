package compuquest.simulation.intellect

import compuquest.simulation.combat.attack
import compuquest.simulation.general.*
import godot.PhysicsDirectSpaceState
import godot.core.variantArrayOf
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.happening.Events

data class Spirit(
  val actionChanceAccumulator: Int = 0,
  val lastTriedAction: Id? = null,
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

  return otherBody != null
      && body.translation.distanceTo(otherBody.translation) <= range
      && space.intersectRay(body.translation, otherBody.translation, variantArrayOf(world.bodies[bodyId]!!, otherBody))
    .none()
}

fun filterEnemyTargets(
  world: World,
  actor: Id,
  character: Character,
  actions: Table<Accessory>
): Map<Id, Character> {
  val deck = world.deck
  val bodies = deck.bodies
  val body = bodies[character.body] ?: return mapOf()
  val space = getSpace(world) ?: return mapOf()
  val range = actions.maxOfOrNull { it.value.range } ?: 0f
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

fun getNextActionAndTarget(
  world: World,
  actor: Id,
  character: Character,
  actions: Table<Accessory>
): Triple<Id, Accessory, Id>? {
  val deck = world.deck
  val bodies = deck.bodies
  val body = bodies[character.body] ?: return null
  val options = filterEnemyTargets(world, actor, character, actions)

  return if (options.any()) {
    val target = world.dice.takeOne(options.entries)
    val targetBody = bodies[target.value.body]!!
    val distance = body.translation.distanceTo(targetBody.translation)
    val action = actions.entries.first { it.value.range >= distance }
    Triple(action.key, action.value, target.key)
  } else
    null
}

fun tryUseAction(world: World, actor: Id, character: Character): Events {
  val deck = world.deck
  val readyActions = getAccessories(deck.accessories, actor)
    .filter { canUse(world, it.value) }

  return if (readyActions.any()) {
    val option = getNextActionAndTarget(world, actor, character, readyActions)
    if (option != null) {
      val (action, accessory, target) = option
      attack(world, actor, character, action, accessory, target)
    } else
      listOf()
  } else
    listOf()
}

fun eventsFromSpirit(world: World, actor: Id): Events {
  val deck = world.deck
  val character = deck.characters[actor]!!
  return if (character.isAlive)
    tryUseAction(world, actor, character)
  else
    listOf()
}

fun updateSpirit(world: World, events: Events): (Id, Spirit): Spirit = { actor, spirit ->
  val characterEvents = events.filter { it.target == actor }
  val 
}
