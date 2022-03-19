package compuquest.simulation.general

import compuquest.simulation.definition.Definitions
import compuquest.simulation.happening.UseAction
import compuquest.simulation.happening.useActionEvent
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.NextId
import silentorb.mythic.ent.Table
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues
import silentorb.mythic.happening.filterEventsByType
import silentorb.mythic.happening.newEvent
import silentorb.mythic.timing.IntTimer
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
	val spawnsCharacter: Key? = null,
	val spawnsScene: String? = null,
	val buff: Key = "",
	val speed: Float = 0f,
	val interval: Int = 0,
	val duration: Float = 0f,
	val recipient: EffectRecipient,
) {
	val strengthInt: Int get() = strength.toInt()
	val durationInt: Int get() = floatToIntTime(duration)
	val isAttack: Boolean = type == AccessoryEffects.damage && recipient == EffectRecipient.projectile
}

data class AccessoryDefinition(
	val cooldown: Float = 0f,
	val key: String,
	val name: String = key,
	val range: Float = 0f,
	val cost: ResourceMap = mapOf(),
	val attributes: Set<Key> = setOf(),
	val actionEffects: List<AccessoryEffect> = listOf(),
	val passiveEffects: List<AccessoryEffect> = listOf(),
	val duration: Float = 0f,
	val animation: Key? = null,
	val stackable: Boolean = false,
	val consumable: Boolean = false,
	val equippedFrame: Int = -1,
	val effectDelaysCooldown: Key? = null, // Cooldown does not decrease while the actor is under this effect
) {
	fun hasAttribute(attribute: String): Boolean = attributes.contains(attribute)
	val isAttack: Boolean = actionEffects.any { it.isAttack }
}

data class Accessory(
	val owner: Id,
	val level: Int = 1,
	val cooldown: Float = 0f,
	val definition: AccessoryDefinition,
) {
	val canBeActivated: Boolean = definition.actionEffects.any()
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

fun newAccessory(definitions: Definitions, nextId: NextId, owner: Id, type: Key, duration: Int = -1): Hand {
	val definition = definitions.accessories[type] ?: throw Error("Invalid accessory type $type")
	val duration2 = when {
		duration != -1 -> duration
		definition.duration != 0f -> floatToIntTime(definition.duration)
		else -> -1
	}
	val timer = if (duration2 > 0)
		IntTimer(duration2)
	else
		null

	return Hand(
		id = nextId(),
		components = listOfNotNull(
			Accessory(
				owner = owner,
				definition = definition,
			),
			timer,
		)
	)
}

fun newAccessory(world: World, owner: Id, type: Key, duration: Int = -1): Hand =
	newAccessory(world.definitions, world.nextId.source(), owner, type, duration)

fun getUsedAccessories(events: Events): Collection<Id> =
	filterEventValues<UseAction>(useActionEvent, events)
		.map { it.action }

const val transferAccessoryEvent = "transferAccessory"

fun transferAccessory(accessory: Id, to: Id) =
	newEvent(transferAccessoryEvent, accessory, to)

fun isDelayedByEffect(accessory: Accessory): Boolean {
//	val effectType = accessory.definition.effectDelaysCooldown
//	effectType != null && )
	return false
}

fun updateAccessory(events: Events, delta: Float): (Id, Accessory) -> Accessory {
	val uses = getUsedAccessories(events)
	val transfers = filterEventsByType<Id>(transferAccessoryEvent, events)

	return { id, accessory ->
		val used = uses.contains(id)
		val cooldown = if (used || accessory.cooldown == 0f || isDelayedByEffect(accessory))
			accessory.definition.cooldown
		else
			max(0f, accessory.cooldown - delta)

		val owner = transfers.firstOrNull { it.target == id }?.value ?: accessory.owner

		accessory.copy(
			cooldown = cooldown,
			owner = owner,
		)
	}
}

fun getConsumedAccessories(accessories: Table<Accessory>, events: Events): Collection<Id> =
	getUsedAccessories(events)
		.filter { accessories[it]?.definition?.consumable == true }

// This function exists to handle redundant non-stackable accessories
fun integrateNewAccessories(
	accessories: Table<Accessory>,
	newAccessories: Table<Accessory>
): Table<Accessory> {
	val duplicates = newAccessories.mapNotNull { a ->
		val existing = accessories.entries.firstOrNull {
			!it.value.definition.stackable && it.value.owner == a.value.owner && it.value.definition == a.value.definition
		}

		if (existing != null)
			existing to a
		else
			null
	}

	val uniqueAdditions = newAccessories - duplicates.map { it.second.key }
	val refreshed = duplicates
		.filter { it.first.value.definition.duration > 0 }
		.map { it.first.key }
		.distinct()

	return accessories + uniqueAdditions
}

fun refreshTimers(timers: Table<IntTimer>, refreshed: Collection<Id>) =
	refreshed.associateWith { id ->
		val timer = timers[id]!!
		timer.copy(
			remaining = timer.duration
		)
	}

fun getOwnerAccessories(accessories: Table<Accessory>, owner: Id): Table<Accessory> =
	accessories.filter { it.value.owner == owner }

fun getOwnerAccessories(world: World, owner: Id): Table<Accessory> =
	getOwnerAccessories(world.deck.accessories, owner)

fun canHeal(accessory: Accessory): Boolean =
	accessory.definition.actionEffects
		.any { it.type == AccessoryEffects.heal }

fun forEachEffectOfType(type: Key): (AccessoryDefinition, (AccessoryEffect) -> Events) -> Events = { definition, map ->
	definition.actionEffects
		.filter { it.type == type }
		.flatMap(map)
}
