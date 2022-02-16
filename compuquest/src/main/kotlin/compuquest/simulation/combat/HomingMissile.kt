package compuquest.simulation.combat

import compuquest.simulation.general.*
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.newEvent

data class HomingMissile(
  val damage: Int,
  val target: Id,
  val owner: Id,
  val speed: Float,
)

fun eventsFromHomingMissile(world: World, delta: Float): (Id, HomingMissile) -> Events = { actor, missile ->
  val deck = world.deck
  val body = deck.bodies[actor]
  val targetBody = deck.bodies[missile.target]
  if (body != null && targetBody != null) {
    val vector = targetBody.translation - body.translation
    val distance = vector.length()
    if (distance < 1f)
      listOf(
        Event(damageEvent, missile.target, missile.damage),
        newEvent(deleteEntityCommand, actor),
        newEvent(detrimentalEffectCommand, missile.target)
      )
    else {
      val offset = vector.normalized() * missile.speed * delta;
      body.translation += offset
      listOf()
    }
  } else
    listOf()
}
