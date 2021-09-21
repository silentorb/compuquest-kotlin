package compuquest.simulation.combat

import compuquest.simulation.general.*
import godot.Spatial
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

fun attack(world: World, actor: Id, character: Character, action: Id, accessory: Accessory, target: Id): Events {
  val spawns = accessory.spawns!!
  val projectileBody = instantiateScene<Spatial>(spawns)
  val actorBody = world.deck.bodies[character.body]!!
  projectileBody?.translation = actorBody.translation + Vector3(0f, -1f, 0f)
  val projectile = Hand(
    components = listOfNotNull(
      HomingMissile(
        damage = accessory.strength.toInt(),
        target = target,
        owner = actor,
        speed = 30f,
      ),
      projectileBody,
    ),
  )

  return listOf(
    Event(useActionCommand, action),
    newHandCommand(projectile),
  )
}
