package compuquest.simulation.combat

import compuquest.simulation.general.Hand
import compuquest.simulation.general.World
import compuquest.simulation.general.newHandEvent
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

//fun missileAttack(world: World, attacker: Id, weapon: Weapon, targetLocation: Vector3?, targetEntity: Id?): Events {
//  val (origin, vector) = getAttackerOriginAndFacing(world.deck, attacker, targetLocation, targetEntity, 0.8f)
//  return listOf(
//    newHandEvent(
//      Hand(
//        components = listOf(
//          Body(
//            position = origin,
//            velocity = vector * weapon.velocity,
//            scale = Vector3(0.5f)
//          ),
//          Missile(
//            damageRadius = weapon.damageRadius,
//            damageFalloff = weapon.damageFalloff,
//            damages = weapon.damages
//          ),
//        )
//      )
//    )
//  )
//}
