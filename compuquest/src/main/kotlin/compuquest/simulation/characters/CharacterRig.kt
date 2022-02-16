package compuquest.simulation.characters

import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import godot.core.Transform
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id

const val defaultCharacterHeight = 1.2f
const val defaultCharacterRadius = 0.3f

fun getCharacterFacing(deck: Deck, actor: Id): Vector3? {
  val body = deck.bodies[actor] as? CharacterBody
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
