package compuquest.simulation.characters

import compuquest.simulation.combat.DamageDefinition
import compuquest.simulation.combat.DamageDefinitions
import compuquest.simulation.combat.Damages
import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.Deck
import compuquest.simulation.general.ResourceMap
import compuquest.simulation.happening.UseAction
import compuquest.simulation.happening.useActionEvent
import godot.core.Transform
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.happening.*
import silentorb.mythic.timing.floatToIntTime
import kotlin.math.max

enum class EffectRecipient {
	inFront,
	self,
	projectile,
	raycast,
}

object AccessoryEffects {

	// Active (Some can also be passive)
	val armor = "armor"
	val damage = "damage"
	val heal = "heal"
	val jump = "jump"
	val resurrect = "resurrect"
	val summon = "summon"
	val summonAtTarget = "summonAtTarget"
	val buff = "buff"
	val equipPrevious = "equipPreviousAccessory"

	// Passive
	val backstab = "backstab"
	val invisible = "invisible"
	val removeOnUseAny = "removeOnUseAny"
}

data class AccessoryEffect(
	val type: String,
	val strength: Float = 0f,
	val damages: DamageDefinitions = listOf(),
	val damageRadius: Float = 0f, // 0f for no AOE
	val damageFalloff: Float = 0f, // Falloff Exponent
	val spawnsCharacter: Key? = null,
	val spawnsScene: String? = null,
	val buff: Key = "",
	val speed: Float = 0f,
	val interval: Int = 0,
	val duration: Float = 0f,
	val recipient: EffectRecipient,
	val transform: Transform? = null,
	val range: Float = 0f,
	val spawnOnEnd: String = "",
) {
	val strengthInt: Int get() = strength.toInt()
	val durationInt: Int get() = floatToIntTime(duration)
	val isAttack: Boolean = type == AccessoryEffects.damage && recipient == EffectRecipient.projectile
}

enum class AccessorySlot {
	consumable,
	mobility,
	passive,
	primary,
	utility,
}

data class AccessoryDefinition(
	val cooldown: Float = 0f,
	val key: String,
	val name: String = key,
	val slot: AccessorySlot,
	val useRange: Float = 0f,
	val cost: ResourceMap = mapOf(),
	val attributes: Set<Key> = setOf(),
	val actionEffects: List<AccessoryEffect> = listOf(),
	val passiveEffects: List<AccessoryEffect> = listOf(),
	val duration: Float = 0f,
	val animation: Key? = null,
	val stackable: Boolean = false,
	val equippedFrame: Int = -1,
	val cooldownDelayEffect: Key? = null, // Cooldown does not decrease while the actor is under this effect
) {
	fun hasAttribute(attribute: String): Boolean = attributes.contains(attribute)
	fun hasActiveEffect(effect: Key): Boolean = actionEffects.any { it.type == effect }
	val consumable: Boolean get() = slot == AccessorySlot.consumable

	val isAttack: Boolean = actionEffects.any { it.isAttack }
}

const val noDuration = -1

data class Accessory(
	val level: Int = 1,
	val cooldown: Float = 0f,
	val definition: AccessoryDefinition,
	val duration: Int = noDuration,
) {
	val canBeActivated: Boolean = definition.actionEffects.any()
	fun hasActiveEffect(effect: Key): Boolean = definition.hasActiveEffect(effect)
}

fun newPassiveEffect(type: Key) =
	AccessoryEffect(
		type = type,
		recipient = EffectRecipient.self,
	)

fun newPassiveEffects(vararg types: Key) =
	types.map(::newPassiveEffect)

object AccessoryIntervals {
	const val continuous = 1
	const val oneSecond = 60
	const val default = 60
}

const val detrimentalEffectCommand = "detrementalEffect"

fun newAccessory(definitions: Definitions, type: Key, duration: Int = noDuration): Accessory {
	val definition = definitions.accessories[type] ?: throw Error("Invalid accessory type $type")

	return Accessory(
		definition = definition,
		duration = when {
			duration != noDuration -> duration
			definition.duration != 0f -> floatToIntTime(definition.duration)
			else -> noDuration
		}
	)
}

fun getUsedAccessories(events: Events): Collection<GenericEvent<UseAction>> =
	filterEventsByType(useActionEvent, events)

const val transferAccessoryEvent = "transferAccessory"

fun transferAccessory(accessory: Id, to: Id) =
	newEvent(transferAccessoryEvent, accessory, to)

fun isDelayedByEffect(container: AccessoryContainer, accessory: Accessory): Boolean {
	val effectType = accessory.definition.cooldownDelayEffect
	return effectType != null && hasPassiveEffect(container.accessories, effectType)
}

fun updateOwnedAccessory(container: AccessoryContainer, events: Events, delta: Float): (Id, Accessory) -> Accessory {
	return { id, accessory ->
		val used = events.any { it.type == useActionEvent && (it.value as? UseAction)?.action == id }
		val cooldown = if (used)
			accessory.definition.cooldown
		else if (accessory.cooldown == 0f || isDelayedByEffect(container, accessory))
			accessory.cooldown
		else
			max(0f, accessory.cooldown - delta)

		val duration = if (accessory.duration > 0)
			accessory.duration - 1
		else
			accessory.duration

		accessory.copy(
			cooldown = cooldown,
			duration = duration,
		)
	}
}

fun getAccessory(deck: Deck, accessory: Id): Accessory? {
	val unOwned = deck.accessories[accessory]
	if (unOwned != null)
		return unOwned

	for (container in deck.containers.values) {
		val owned = container.accessories[accessory]
		if (owned != null)
			return owned
	}

	return null
}

fun getConsumedAccessories(deck: Deck, events: Events): Collection<Id> =
	getUsedAccessories(events)
		.filter {
			deck.containers[it.target]
				?.accessories
				?.getOrDefault(it.value.action, null)
				?.definition?.consumable == true
		}
		.map { it.value.action }

// This function exists to handle redundant non-stackable accessories
//fun integrateNewAccessories(
//	accessories: Table<Accessory>,
//	newAccessories: Table<Accessory>
//): Table<Accessory> {
//	val duplicates = newAccessories.mapNotNull { a ->
//		val existing = accessories.entries.firstOrNull {
//			!it.value.definition.stackable && it.value.owner == a.value.owner && it.value.definition == a.value.definition
//		}
//
//		if (existing != null)
//			existing to a
//		else
//			null
//	}
//
//	val uniqueAdditions = newAccessories - duplicates.map { it.second.key }
//	val refreshed = duplicates
//		.filter { it.first.value.definition.duration > 0 }
//		.map { it.first.key }
//		.distinct()
//
//	return accessories + uniqueAdditions
//}

//fun refreshTimers(timers: Table<IntTimer>, refreshed: Collection<Id>) =
//	refreshed.associateWith { id ->
//		val timer = timers[id]!!
//		timer.copy(
//			remaining = timer.duration
//		)
//	}

fun canHeal(accessory: Accessory): Boolean =
	accessory.definition.actionEffects
		.any { it.type == AccessoryEffects.heal }

fun forEachEffectOfType(type: Key): (AccessoryDefinition, (AccessoryEffect) -> Events) -> Events = { definition, map ->
	definition.actionEffects
		.filter { it.type == type }
		.flatMap(map)
}
