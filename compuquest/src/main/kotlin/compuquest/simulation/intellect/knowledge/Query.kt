package compuquest.simulation.intellect.knowledge

import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.characters.getAccessoriesWithEffect
import compuquest.simulation.characters.hasAccessoryWithEffect
import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.Deck
import silentorb.mythic.ent.Id

fun isAParent(knowledge: Knowledge): Boolean =
	knowledge.relationships.any { it.isA == RelationshipType.parent }

fun hasFood(deck: Deck, actor: Id): Boolean =
	hasAccessoryWithEffect(deck.accessories, actor, AccessoryEffects.heal)

fun getFood(deck: Deck, actor: Id) =
	getAccessoriesWithEffect(deck.accessories, actor, AccessoryEffects.heal)
