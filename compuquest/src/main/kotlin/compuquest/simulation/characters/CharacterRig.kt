package compuquest.simulation.characters

import compuquest.simulation.general.World
import godot.Spatial
import godot.core.Transform
import godot.core.Vector3
import godot.global.PI
import scripts.entities.Player
import silentorb.mythic.ent.Id

const val defaultCharacterHeight = 1.2f

fun getCharacterFacing(world: World, actor: Id): Vector3? {
  val body = world.bodies[actor] as? Player
  val head = body?.head
  return if (head != null) {
    -Transform().rotated(Vector3.LEFT, -head.rotation.x).rotated(Vector3.UP, body.rotation.y).basis.z
//    Transform().rotated(Vector3.UP, PI / 2f).rotated(Vector3.LEFT, PI / 4f).basis.x
//    val i = Transform().rotated(Vector3.LEFT, PI / 2f).rotated(Vector3.UP, PI / 2f)
//    val k = Transform().rotated(Vector3.UP, PI / 2f).rotated(Vector3.LEFT, PI / 2f)
//    body.transform.rotated(Vector3.UP, head.rotation.y).basis.z
  } else
    null
//  deck.characterRigs[attacker]!!.facingVector
}
