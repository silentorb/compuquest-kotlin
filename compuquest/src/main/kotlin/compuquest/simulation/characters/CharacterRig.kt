package compuquest.simulation.characters

import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import godot.Spatial
import godot.core.Transform
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id

const val defaultCharacterHeight = 1.2f
const val defaultCharacterRadius = 0.3f

fun getSpatialFacing(body: Spatial, head: Spatial) =
	-Transform().rotated(Vector3.LEFT, -head.rotation.x).rotated(Vector3.UP, body.rotation.y).basis.z

fun getCharacterFacing(deck: Deck, actor: Id): Vector3? {
	val body = deck.bodies[actor] as? CharacterBody
	val head = body?.head
	return when {
		head != null -> getSpatialFacing(body, head)
		body != null -> getSpatialFacing(body, body)
		else -> null
	}
}
