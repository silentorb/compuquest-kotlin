package compuquest.simulation.characters

import compuquest.simulation.general.World
import godot.Spatial
import godot.core.Vector3
import silentorb.mythic.ent.Id

const val defaultCharacterHeight = 1.2f

fun getCharacterFacing(world: World, actor: Id): Vector3? {
  val body = world.bodies[actor]
  val head = body?.get("head") as? Spatial
  return if (head != null) {
    (body.rotation * head.rotation)
  }
  else
  null
//  deck.characterRigs[attacker]!!.facingVector
}
