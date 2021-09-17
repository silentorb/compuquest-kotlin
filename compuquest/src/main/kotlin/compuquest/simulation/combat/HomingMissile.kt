package compuquest.simulation.combat

import compuquest.simulation.general.World
import compuquest.simulation.general.deleteEntityCommand
import compuquest.simulation.general.modifyHealthCommand
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.ent.Id

data class HomingMissile(
  val damage: Int,
  val target: Id,
  val owner: Id,
  val speed: Float,
)

fun eventsFromHomingMissile(world: World, delta: Float): (Id, HomingMissile) -> Events = { actor, missile ->
  val deck = world.deck
  val body = world.bodies[actor]
  val targetCharacter = deck.characters[missile.target]
  val targetBody = world.bodies[targetCharacter?.body]
  if (body != null && targetBody != null) {
    val vector = targetBody.translation - body.translation
    val distance = vector.length()
    if (distance < 1f)
      listOf(
        Event(modifyHealthCommand, missile.target, -missile.damage),
        Event(deleteEntityCommand, actor)
      )
    else {
      val offset = vector.normalized() * missile.speed * delta;
      body.translation += offset
      listOf()
    }
  } else
    listOf()
}
