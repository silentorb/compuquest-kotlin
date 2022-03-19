package compuquest.simulation.combat

import compuquest.definition.Accessories
import compuquest.simulation.characters.hasAccessoryOfType
import compuquest.simulation.general.Deck
import silentorb.mythic.ent.Id

fun isInvisible(deck: Deck, actor: Id) =
	hasAccessoryOfType(deck, actor, Accessories.invisible)
