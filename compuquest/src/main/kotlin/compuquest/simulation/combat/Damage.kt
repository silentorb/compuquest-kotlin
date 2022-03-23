package compuquest.simulation.combat

import compuquest.simulation.characters.*
import compuquest.simulation.general.*
import godot.Node
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

const val defaultDamageMultiplier = 100
typealias DamageMultipliers = Map<DamageType, Percentage>

data class Damage(
	val amount: Int,
	val type: DamageType = "",
	val source: Id = emptyId,
	val sourceLocation: Vector3? = null
)

const val damageEvent = "damage"

fun newDamage(actor: Id, damage: Damage) =
	Event(damageEvent, actor, damage)

fun calculateDamage(deck: Deck, actor: Id, weapon: Accessory, effect: AccessoryEffect): Int {
	val baseDamage = effect.strengthInt
	val accessories = getOwnerAccessories(deck, actor)
	val backStabModifier = if (
		hasPassiveEffect(accessories, AccessoryEffects.invisible) &&
		hasPassiveEffect(accessories, AccessoryEffects.backstab)
	)
		2
	else
		1

	return baseDamage * backStabModifier
}

//fun applyDamage(deck: Deck, actor: Id, characterEvents: Events): Int {
//	val damages = filterEventValues<Int>(damageEvent, characterEvents)
//	return if (damages.any()) {
//		val damageReduction = getOwnerAccessories(deck, actor)
//			.values
//			.sumOf { accessory ->
//				accessory.definition.actionEffects
//					.filter { it.type == AccessoryEffects.armor }
//					.sumOf { it.strengthInt }
//			}
//
//		damages.sumOf { -max(0, it - damageReduction) }
//	} else
//		0
//}

fun applyDamageMods(multipliers: DamageMultipliers): (Damage) -> Int = { damage ->
	val mod = multipliers[damage.type]
	if (mod == null)
		damage.amount
	else
		applyMultiplier(damage.amount, mod)
}

fun aggregateDamage(multipliers: DamageMultipliers, damages: List<Damage>) =
	damages.sumOf(applyDamageMods(multipliers))

fun aggregateHealthModifiers(destructible: Destructible, damages: List<Damage>): Int {
	val damage = aggregateDamage(destructible.damageMultipliers, damages)
	return -damage
}

typealias DamageModifierQuery = (Id) -> (DamageType) -> List<Int>

fun calculateDamageMultipliers(
	damageTypes: Set<DamageType>,
	modifierQuery: DamageModifierQuery,
	id: Id,
	base: DamageMultipliers
): DamageMultipliers {
	val query = modifierQuery(id)
	return damageTypes.map { damageType ->
		val baseMultiplier = base[damageType] ?: defaultDamageMultiplier
		val modifiers = query(damageType)
		val aggregate = modifiers.sum()
		val value = baseMultiplier + aggregate
		Pair(damageType, value)
	}.associate { it }
}

fun applyDamage(deck: Deck, source: Id, sourceLocation: Vector3?, target: Id, node: Node, amount: Int): Events {
	val damageNodeEvents = getDamageNodeEvents(node, amount)
	return if (deck.characters.containsKey(target))
		damageNodeEvents + newDamage(target, Damage(amount = amount, source = source, sourceLocation = sourceLocation))
	else
		damageNodeEvents
}
