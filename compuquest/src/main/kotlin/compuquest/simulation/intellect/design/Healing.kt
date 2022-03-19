package compuquest.simulation.intellect.design

import compuquest.simulation.characters.*
import compuquest.simulation.general.*
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.knowledge.Knowledge
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import kotlin.math.abs

fun getHealingAccessories(deck: Deck, actor: Id, recipient: EffectRecipient): Table<Accessory> =
	getReadyAccessories(deck, actor)
		.filter { (_, accessory) ->
			accessory.definition.actionEffects.any {
				it.type == AccessoryEffects.heal && it.recipient == recipient
			}
		}

fun getAccessoryHealAmount(accessory: Accessory): Int =
	accessory.definition.actionEffects
		.sumOf {
			if (it.type == AccessoryEffects.heal)
				it.strengthInt
			else
				0
		}

fun getMostEfficientHealingAccessory(accessories: Table<Accessory>, gap: Int): Map.Entry<Id, Accessory> =
	accessories.map { entry ->
		val amount = getAccessoryHealAmount(entry.value)
		Pair(entry, abs(gap - amount))
	}.minByOrNull { it.second }!!
		.first

fun getMostEfficientHealingAccessory(deck: Deck, actor: Id, gap: Int): Map.Entry<Id, Accessory>? {
	val healingAccessories = getHealingAccessories(deck, actor, EffectRecipient.self)
	return if (healingAccessories.any())
		getMostEfficientHealingAccessory(healingAccessories, gap)
	else
		null
}

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

fun checkHealing(world: World, actor: Id, spirit: Spirit, knowledge: Knowledge): Goal? {
	val goal = spirit.goal
	val accessory = getHealingAccessories(world.deck, actor, EffectRecipient.raycast)
		.maxByOrNull { it.value.definition.range }

	return if (accessory != null) {
		val visibleTarget = knowledge.visibleAllies
			.maxByOrNull { (_, character) -> character.definition.health - character.health }

		useActionOnTarget(world, actor, knowledge, accessory, visibleTarget?.key, goal)
	} else
		null
}
