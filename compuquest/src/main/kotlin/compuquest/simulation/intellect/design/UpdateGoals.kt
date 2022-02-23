package compuquest.simulation.intellect.design

import compuquest.simulation.characters.Character
import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.knowledge.Knowledge
import silentorb.mythic.ent.Id

fun updateGoals(world: World, actor: Id, spirit: Spirit, knowledge: Knowledge): Goal {
	val goal = spirit.goal
	val deck = world.deck
	val character = deck.characters[actor]
	return if (character == null)
		goal
	else
		checkSelfHealing(deck, actor, character, spirit)
			?: checkParenting(world, actor, character, spirit)
			?: checkHealing(world, actor, spirit, knowledge)
			?: checkCombat(world, actor, spirit, knowledge)
			?: checkPathDestinations(world, actor, spirit)
			?: checkRoaming(world, actor, spirit)
}
