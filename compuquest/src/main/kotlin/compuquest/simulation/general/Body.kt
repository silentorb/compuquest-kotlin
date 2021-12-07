package compuquest.simulation.general

import godot.core.Vector3
import silentorb.mythic.ent.Id

data class Body(
  val translation: Vector3,
  val rotation: Vector3,
  val velocity: Vector3 = Vector3.ZERO,
)

fun updateBody(world: World, delta: Float): (Id, Body) -> Body = { actor, body ->
  body
}
