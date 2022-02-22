package compuquest.simulation.characters

import compuquest.simulation.combat.getToolOffset
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
	val spatialBody = body as? Spatial
	return when {
		head != null -> getSpatialFacing(spatialBody!!, head)
		body != null -> getSpatialFacing(spatialBody!!, spatialBody)
		else -> null
	}
}

fun getCharacterOriginAndFacing(world: World, actor: Id, forwardOffset: Float = 0f): Pair<Vector3?, Vector3> {
	val deck = world.deck
	val body = deck.bodies[actor]!!
	val toolOffset = getToolOffset(world, actor)
	val baseOrigin = body.translation + toolOffset
	val vector = getCharacterFacing(deck, actor)

	return if (vector == null)
		null to Vector3.ZERO
	else {
		val origin = baseOrigin + vector * forwardOffset
		Pair(origin, vector)
	}
}
