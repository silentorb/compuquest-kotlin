package compuquest.simulation.general

import compuquest.simulation.definition.Definitions
import compuquest.simulation.happening.UseAction
import compuquest.simulation.happening.useActionEvent
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.NextId
import silentorb.mythic.ent.Table
import silentorb.mythic.happening.Events
import silentorb.mythic.timing.IntTimer
import silentorb.mythic.timing.floatToIntTime
import silentorb.mythic.timing.newTimer
import kotlin.math.max

data class AccessoryEffect(
	val type: String,
	val strength: Float = 0f,
	val spawns: Key? = null,
	val speed: Float = 0f,
	val interval: Int = 0,
	val duration: Float = 0f,
) {
	val strengthInt: Int get() = strength.toInt()
}

data class AccessoryDefinition(
	val cooldown: Float = 0f,
	val name: String,
	val range: Float = 0f,
	val cost: ResourceMap = mapOf(),
	val attributes: Set<Key> = setOf(),
	val actionEffects: List<AccessoryEffect> = listOf(), // Currently only zero or one action effects are fully supported.  Stored as an array to minimize future refactoring
	val passiveEffects: List<AccessoryEffect> = listOf(),
	val duration: Float = 0f,
	val animation: Key? = null,
	val stackable: Boolean = false,
) {
	fun hasAttribute(attribute: String): Boolean = attributes.contains(attribute)
}

data class Accessory(
	val owner: Id,
	val level: Int? = null,
	val cooldown: Float = 0f,
	val definition: AccessoryDefinition,
	val duration: Int = -1,
)

object AccessoryEffects {
	val armor = "armor"
	val attack = "attack"
	val damageSelf = "damageSelf"
	val heal = "heal"
	val resurrect = "resurrect"
	val summonAtTarget = "summonAtTarget"
}

object AccessoryAttributes {
	const val attack = "attack"
}

object AccessoryIntervals {
	const val oneSecond = 60
	const val default = 60
}

const val detrimentalEffectCommand = "detrementalEffect"

fun newAccessory(definitions: Definitions, nextId: NextId, owner: Id, type: Key): Hand {
	val definition = definitions.accessories[type] ?: throw Error("Invalid accessory type $type")
	val duration = if (definition.duration == 0f)
		-1
	else
		floatToIntTime(definition.duration)

	return Hand(
		id = nextId(),
		components = listOf(
			Accessory(
				owner = owner,
				definition = definition,
				duration = duration
			),
		)
	)
}

fun updateAccessory(events: Events, delta: Float): (Id, Accessory) -> Accessory {
	val uses = events
		.filter { it.type == useActionEvent }
		.mapNotNull { (it.value as? UseAction)?.action }
	return { id, accessory ->
		val used = uses.contains(id)
		val cooldown = if (used)
			accessory.definition.cooldown
		else
			max(0f, accessory.cooldown - delta)

		accessory.copy(
			cooldown = cooldown
		)
	}
}

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
