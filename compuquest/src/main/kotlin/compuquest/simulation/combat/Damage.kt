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
typealias DamageType = String

data class DamageDefinition(
	val amount: Int,
	val type: DamageType,
)

data class Damage(
	val amount: Int,
	val type: DamageType,
	val source: Id = emptyId,
)

typealias Damages = List<Damage>
typealias DamageDefinitions = List<DamageDefinition>

const val damageEvent = "damage"

data class DamageEvent(
	val damages: Damages,
	val position: Vector3? = null,
	val sourceLocation: Vector3? = null,
) {
	val source: Id
		get() = damages.firstOrNull { it.source != emptyId }?.source ?: emptyId

	val amount: Int
		get() = damages.sumOf { it.amount }
}

fun damagesOf(vararg damages: Pair<DamageType, Int>): DamageDefinitions =
	damages.map {
		DamageDefinition(
			type = it.first,
			amount = it.second,
		)
	}

fun newDamageEvents(
	target: Id,
	damages: List<Damage>,
	position: Vector3? = null,
	sourceLocation: Vector3? = null
): Events =
	listOf(
		Event(
			type = damageEvent,
			target = target,
			value = DamageEvent(
				damages = damages,
				position = position,
				sourceLocation = sourceLocation,
			)
		)
	)

fun calculateDamage(deck: Deck, actor: Id, damage: DamageDefinition): Int {
	val baseDamage = damage.amount
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

fun newDamages(
	deck: Deck,
	actor: Id,
	effect: AccessoryEffect
): Damages =
	effect.damages
		.map { definition ->
			Damage(
				type = definition.type,
				amount = calculateDamage(deck, actor, definition),
				source = actor,
			)
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

fun applyDamage(
	deck: Deck,
	sourceLocation: Vector3?,
	target: Id,
	node: Node,
	damages: List<Damage>
): Events {
	val damageNodeEvents = getDamageNodeEvents(node, damages)
	return if (deck.characters.containsKey(target))
		damageNodeEvents + newDamageEvents(target, damages, null, sourceLocation)
	else
		damageNodeEvents
}
