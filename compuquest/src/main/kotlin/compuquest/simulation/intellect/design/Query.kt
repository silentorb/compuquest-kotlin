package compuquest.simulation.intellect.design

import compuquest.simulation.characters.getAccessoriesSequence
import compuquest.simulation.general.Accessory
import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.Deck
import compuquest.simulation.general.canHeal
import compuquest.simulation.physics.Body
import silentorb.mythic.ent.Id
import kotlin.math.abs

typealias AccessoryMapList = List<Map.Entry<Id, Accessory>>

fun getReadyHealingAccessories(deck: Deck, actor: Id): AccessoryMapList =
	getAccessoriesSequence(deck.accessories, actor)
		.filter { it.value.cooldown == 0f && canHeal(it.value) }
		.toList()

fun getAccessoryHealAmount(accessory: Accessory): Int =
	accessory.definition.actionEffects
		.sumOf {
			if (it.type == AccessoryEffects.heal)
				it.strengthInt
			else
				0
		}

fun getMostEfficientHealingAccessory(accessories: AccessoryMapList, gap: Int): Map.Entry<Id, Accessory> =
	accessories.map { entry ->
		val amount = getAccessoryHealAmount(entry.value)
		Pair(entry, abs(gap - amount))
	}.minByOrNull { it.second }!!
		.first

fun getMostEfficientHealingAccessory(deck: Deck, actor: Id, gap: Int): Map.Entry<Id, Accessory>? {
	val healingAccessories = getReadyHealingAccessories(deck, actor)
	return if (healingAccessories.any())
		getMostEfficientHealingAccessory(healingAccessories, gap)
	else
		null
}

fun getPathDestinations(goal: Goal, body: Body?) =
	if (goal.pathDestinations.any() && body != null &&
		body.translation.distanceTo(goal.pathDestinations.first()) < 1f
	)
		goal.pathDestinations.drop(1)
	else
		goal.pathDestinations
