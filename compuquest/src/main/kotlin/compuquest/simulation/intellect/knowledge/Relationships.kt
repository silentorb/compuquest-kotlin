package compuquest.simulation.intellect.knowledge

import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.characters.getCharacterGroups
import compuquest.simulation.general.World
import silentorb.mythic.ent.Id

fun hasRelationship(world: World, actor: Id?, other: Id?, type: RelationshipType): Boolean =
	if (actor == null || other == null)
		false
	else {
		val deck = world.deck
		val relationships = deck.spirits[actor]?.knowledge?.relationships ?: listOf()
		val otherCharacter = deck.characters[other]
		if (otherCharacter != null) {
			val groups = getCharacterGroups(deck, otherCharacter).toList()
			relationships.any { it.isA == type && (it.of == other || groups.contains(it.of)) }
		} else
			false
	}

fun isEnemy(world: World, first: Id?, second: Id?): Boolean =
	hasRelationship(world, first, second, RelationshipType.enemy)

fun isFriendly(world: World, first: Id?, second: Id?): Boolean =
	!isEnemy(world, first, second)
