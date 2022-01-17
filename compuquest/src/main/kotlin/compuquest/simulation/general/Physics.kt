package compuquest.simulation.general

import godot.Spatial
import godot.core.Vector3
import godot.core.variantArrayOf
import silentorb.mythic.ent.Id

// Eventually may need to return more information such as hit location but just the id is enough for now
fun castCharacterRay(world: World, actor: Id, maxDistance: Float): Id? {
  val space = world.space
  val body = world.bodies[actor]!!
  val target = body.translation - body.transform.basis.z * maxDistance
  val result = space.intersectRay(body.translation, target, variantArrayOf(body)).toMap()
  val collider = result["collider"] as? Spatial
  val position = result["position"] as? Vector3
  return if (collider != null && position != null)
    world.bodies.entries
      .firstOrNull { it.value == collider }
      ?.key
  else
    null
}
