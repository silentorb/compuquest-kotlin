package compuquest.simulation.combat

import compuquest.simulation.general.Accessory
import compuquest.simulation.general.Hand
import compuquest.simulation.general.World
import compuquest.simulation.general.newHandEvent
import godot.core.Vector3
import scripts.entities.Projectile
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Events

fun missileAttack(world: World, actor: Id, weapon: Accessory, targetLocation: Vector3?, targetEntity: Id?): Events {
  val originAndFacing = getAttackerOriginAndFacing(world, actor, targetLocation, targetEntity, 0.8f)
  return if (originAndFacing == null)
    listOf()
  else {
    val (origin, velocity) = originAndFacing
    val definition = weapon.definition
    val effect = definition.effects.first()
    val projectile = instantiateScene<Projectile>(effect.spawns!!)!!
    projectile.translation = origin
    projectile.origin = origin
    projectile.range = definition.range
    projectile.velocity = velocity * effect.speed
    projectile.ignore = world.bodies[actor]
//    projectile.addCollisionExceptionWith(world.bodies[actor]!!)
//    projectile.applyCentralImpulse(velocity * effect.speed)
//    world.scene!!.addChild(projectile)

    listOf(
      newHandEvent(
        Hand(
          components = listOf(
            projectile,
//////            Body(
//////              position = origin,
//////              velocity = vector * weapon.velocity,
//////              scale = Vector3(0.5f)
//////            ),
            Missile(
              velocity = velocity * effect.speed,
              damage = effect.strengthInt,
//              damages = weapon.damages
            ),
          )
        )
      )
    )
  }
}
