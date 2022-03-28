package compuquest.simulation.characters

import compuquest.simulation.general.Deck
import compuquest.simulation.general.Int100
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key

typealias ProficiencyLevels = Map<Key, Int>

const val baseProficienciesLevelIncrement = 20

fun getProficiencyModifier(required: Set<Key>, available: ProficiencyLevels): Int100 =
	if (required.none())
		100
	else {
		val baseHighLevelMod = available
			.filter { required.contains(it.key) }
			.values.sum()

		val dilutionBonus = if (required.size > 1)
			required.minOf { available[it] ?: 0 }
		else
			0

		val highLevelMod = baseHighLevelMod + dilutionBonus

		val mod = highLevelMod * baseProficienciesLevelIncrement / required.size
		100 - baseProficienciesLevelIncrement + mod
	}

fun getProficienceyModifier(effect: AccessoryEffect, available: ProficiencyLevels): Int100 =
	getProficiencyModifier(effect.proficiencies, available)

fun getCharacterProficiencies(deck: Deck, actor: Id) =
	deck.characters[actor]!!.definition.proficiencies

fun getProficienceyModifier(deck: Deck, effect: AccessoryEffect, actor: Id): Int100 =
	getProficienceyModifier(effect, getCharacterProficiencies(deck, actor))
