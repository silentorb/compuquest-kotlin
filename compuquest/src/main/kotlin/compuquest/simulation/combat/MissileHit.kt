package compuquest.simulation.combat

import compuquest.simulation.general.World
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events
import silentorb.mythic.spatial.minMax

import kotlin.math.pow
import kotlin.math.roundToInt

fun applyFalloff(fallOff: Float, range: Float, damages: List<DamageDefinition>, distance: Float): List<DamageDefinition> {
  val fallOffModifier = fallOff.pow(minMax(distance / range, 0f, 1f))
  return damages.map { damage ->
    damage.copy(
        amount = (damage.amount.toFloat() * fallOffModifier).roundToInt()
    )
  }
}

//fun eventsFromMissileCollision(world: World, id: Id, missile: Missile, collision: Collision): Events {
//  val deck = world.deck
//  val origin = deck.bodies[id]!!.translation
//  val damageRadius = missile.damageRadius
//  val damageEvents = deck.bodies
//      .minus(id)
//      .filter { it.value.translation.distanceTo(origin) < damageRadius }
//      .mapNotNull { (target, targetBody) ->
//        val destructible = deck.destructibles[target]
//        if (destructible != null) {
//          val distance = targetBody.translation.distanceTo(origin)
//          val damages = applyFalloff(missile.damageFalloff, damageRadius, missile.damages, distance)
//          newDamageEvents(target, id, damages, position = getCenter(origin, targetBody.translation))
//        } else
//          null
//      }
//      .flatten()
//
//  val body = world.deck.bodies[id]!!
//  val explosionDepiction = NewHand(
//      components = listOf(
//          Body(
//              position = body.position,
//              scale = Vector3(missile.damageRadius)
//          ),
//          Depiction(
//              type = DepictionType.staticMesh,
//              mesh = Meshes.sphere
//          ),
//          FloatTimer(0.15f)
//      )
//  )
//  return listOf(DeleteEntityEvent(id)) + damageEvents + explosionDepiction
//}

//fun eventsFromMissiles(world: SpatialCombatWorld, collisions: CollisionMap): Events =
//    world.deck.missiles
//        .mapNotNull { (id, missile) ->
//          val collision = collisions[id]
//          if (collision != null) {
//            eventsFromMissileCollision(world, id, missile, collision)
//          } else
//            null
//        }
//        .flatten()
