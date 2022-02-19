package compuquest.simulation.intellect.design

import compuquest.simulation.characters.Character
import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.knowledge.Knowledge
import silentorb.mythic.ent.Id

fun checkSelfHealing(deck: Deck, actor: Id, character: Character): Id? {
	val health = character.health
	val maxHealth = character.destructible.maxHealth
	val gap = maxHealth - health
	val mostEfficient = getMostEfficientHealingAccessory(deck, actor, gap)
	return if (mostEfficient != null) {
		val healAmount = getAccessoryHealAmount(mostEfficient.value)
		val excess = health + healAmount - maxHealth
		if (excess < maxHealth + (maxHealth / 10) || health < maxHealth / 3)
			mostEfficient.key
		else
			null
	} else
		null
}

fun checkSelfHealing(deck: Deck, actor: Id, character: Character, spirit: Spirit): Goal? {
	// This is split into a broad pass and a narrow pass filter for performance
	return if (character.health <= character.destructible.maxHealth * 2 / 3) {
		val healingItem = checkSelfHealing(deck, actor, character)
		if (healingItem != null)
			useActionGoal(spirit.goal, healingItem)
		else
			null
	} else
		null
}

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
