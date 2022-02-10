package compuquest.simulation.intellect.knowledge

import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.general.World
import silentorb.mythic.ent.Id

fun hasRelationship(world: World, actor: Id?, other: Id?, type: RelationshipType): Boolean =
	if (actor == null || other == null)
		false
	else {
		val relationships = world.deck.spirits[actor]?.knowledge?.relationships ?: listOf()
		relationships.any { it.of == other && it.isA == type }
	}

fun isEnemy(world: World, first: Id?, second: Id?): Boolean =
	hasRelationship(world, first, second, RelationshipType.enemy)

fun isFriendly(world: World, first: Id?, second: Id?): Boolean =
	!isEnemy(world, first, second)
