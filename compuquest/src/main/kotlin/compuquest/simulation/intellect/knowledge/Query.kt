package compuquest.simulation.intellect.knowledge

import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.characters.getAccessoriesWithActionEffect
import compuquest.simulation.characters.hasAccessoryWithActionEffect
import compuquest.simulation.characters.AccessoryEffects
import compuquest.simulation.general.Deck
import silentorb.mythic.ent.Id

fun isAParent(knowledge: Knowledge): Boolean =
	knowledge.relationships.any { it.isA == RelationshipType.parent }

fun hasFood(deck: Deck, actor: Id): Boolean =
	hasAccessoryWithActionEffect(deck, actor, AccessoryEffects.heal)

fun getFood(deck: Deck, actor: Id) =
	getAccessoriesWithActionEffect(deck, actor, AccessoryEffects.heal)
