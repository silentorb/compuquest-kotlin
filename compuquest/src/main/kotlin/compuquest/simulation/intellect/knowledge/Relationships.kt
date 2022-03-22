package compuquest.simulation.intellect.knowledge

import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.characters.getCharacterGroups
import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import silentorb.mythic.ent.Id

fun hasRelationship(deck: Deck, actor: Id?, other: Id?, type: RelationshipType): Boolean =
	if (actor == null || other == null)
		false
	else {
		val relationships = deck.spirits[actor]?.knowledge?.relationships ?: listOf()
		val otherCharacter = deck.characters[other]
		if (otherCharacter != null) {
			val groups = getCharacterGroups(deck, otherCharacter).toList()
			relationships.any { it.isA == type && (it.of == other || groups.contains(it.of)) }
		} else
			false
	}

fun isEnemy(deck: Deck, first: Id?, second: Id?): Boolean =
	hasRelationship(deck, first, second, RelationshipType.enemy)

fun isFriendly(deck: Deck, first: Id?, second: Id?): Boolean =
	!isEnemy(deck, first, second)
