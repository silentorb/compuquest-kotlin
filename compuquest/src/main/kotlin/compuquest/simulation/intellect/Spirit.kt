package compuquest.simulation.intellect

import compuquest.godoting.entityFromScene
import compuquest.simulation.combat.HomingMissile
import compuquest.simulation.general.*
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import godot.Spatial
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table

data class Spirit(
  val actionChanceAccumulator: Int = 0,
)

fun inRange(bodies: Table<Body>, body: Body, other: Character, range: Float): Boolean {
  val otherBody = bodies[other.body]
  return otherBody != null
      && body.translation.distanceTo(otherBody.translation) <= range
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

  val range = actions.maxOfOrNull { it.value.range } ?: 0f
  val options = deck.characters
    .filter { (id, other) ->
      id != actor
          && other.faction != character.faction
          && other.isAlive
          && inRange(bodies, body, other, range)
    }

  val target = options.entries.firstOrNull()
  return if (target != null) {
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
      val spawns = accessory.spawns!!
      val projectileBody = entityFromScene<Spatial>(spawns)
      val actorBody = deck.bodies[character.body]!!
      projectileBody?.translation = actorBody.translation + Vector3(0f, -1f, 0f)
      val projectile = Hand(
        components = listOfNotNull(
          HomingMissile(
            damage = 10,
            target = target,
            owner = actor,
            speed = 15f,
          ),
          projectileBody,
        ),
      )
      listOf(
        Event(useActionCommand, action),
        newHandCommand(projectile),
      )
    } else
      listOf()
  } else
    listOf()
}

fun updateSpirit(world: World, actor: Id): Events {
  val deck = world.deck
  val character = deck.characters[actor]!!
  return if (character.isAlive)
    tryUseAction(world, actor, character)
  else
    listOf()
}
