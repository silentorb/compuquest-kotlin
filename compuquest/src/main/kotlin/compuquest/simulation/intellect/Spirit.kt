package compuquest.simulation.intellect

import compuquest.godoting.entityFromScene
import compuquest.simulation.combat.Missile
import compuquest.simulation.general.*
import compuquest.simulation.happening.Event
import compuquest.simulation.happening.Events
import godot.Spatial
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table

data class Spirit(
  val actionChanceAccumulator: Int = 0,
)

fun getNextActionAndTarget(
  world: World,
  actor: Id,
  character: Character,
  actions: Table<Accessory>
): Triple<Id, Accessory, Id>? {
  val deck = world.deck
  val bodies = world.bodies
  val body = bodies[actor]!!
  val range = actions.maxOfOrNull { it.value.range } ?: 0f
  val options = deck.characters
    .filter { (id, other) ->
      id != actor
          && other.faction != character.faction
          && character.isAlive
          && body.translation.distanceTo(bodies[id]!!.translation) <= range
    }

  val target = options.keys.firstOrNull()
  return if (target != null) {
    val targetBody = bodies[target]!!
    val distance = body.translation.distanceTo(targetBody.translation)
    val action = actions.entries.first { it.value.range >= distance }
    Triple(action.key, action.value, target)
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
      val actorBody = world.bodies[actor]!!
      projectileBody?.translation = actorBody.translation + Vector3(0f, -1f, 0f)
      val projectile = Hand(
        components = listOfNotNull(
          Missile(
            damage = 10,
            target = target,
            owner = actor,
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
