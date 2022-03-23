package compuquest.simulation.combat

import compuquest.simulation.characters.*
import compuquest.simulation.definition.ResourceTypes
import compuquest.simulation.general.highIntScale
import compuquest.simulation.general.modifyResource
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId
import silentorb.mythic.happening.*

data class Destructible(
	val health: Int,
	val maxHealth: Int,
	val healthAccumulator: Int = 0,
	val drainDuration: Int = 0,
	val damageMultipliers: DamageMultipliers = mapOf(),
	val lastDamageSource: Id = emptyId,
	val lastDamageLocation: Vector3? = null,
)

// This is intended to be used outside of combat.
// It overrides any other health modifying events.
const val restoreFullHealthEvent = "restoreFullHealth"

fun damageDestructible(damages: List<Damage>): (Destructible) -> Destructible = { destructible ->
	if (damages.none()) {
		destructible
	} else {
		val healthMod = aggregateHealthModifiers(destructible, damages)
		val nextHealth = modifyResource(destructible.health, destructible.maxHealth, healthMod)
		destructible.copy(
			health = nextHealth,
			lastDamageSource = damages.firstOrNull { it.source != emptyId }?.source ?: destructible.lastDamageSource
		)
	}
}

val restoreFullHealth: (Destructible) -> Destructible = { destructible ->
	destructible.copy(
		health = destructible.maxHealth
	)
}

fun modifyDestructible(events: List<ModifyResource>, destructible: Destructible, mod: Int = 0) =
	modifyResourceWithEvents(events, ResourceTypes.health, destructible.health, destructible.maxHealth, mod)

fun modifyHealth(actor: Id, amount: Int) =
	newEvent(modifyResourceEvent, actor, ModifyResource(ResourceTypes.health, amount))

fun prioritizeDamageSourceAttribution(damages: List<Damage>): Damage? =
	if (damages.size < 2)
		damages.firstOrNull()
	else
		damages.filter { it.source != emptyId && it.sourceLocation != null }.maxByOrNull { it.amount }
			?: damages.filter { it.source != emptyId || it.sourceLocation != null }.maxByOrNull { it.amount }
			?: damages.maxByOrNull { it.amount }

fun updateDestructible(actorEvents: Events, isAlive: Boolean, destructible: Destructible): Destructible {
	val damages = if (isAlive)
		filterEventValues<Damage>(damageEvent, actorEvents)
	else
		listOf()

	val mods = if (isAlive)
		filterEventValues<ModifyResource>(modifyResourceEvent, actorEvents)
	else
		listOf()

	val result = if (actorEvents.any { it.type == restoreFullHealthEvent })
		restoreFullHealth(destructible)
	else
		damageDestructible(damages)(destructible)

	val nourishmentAdjustment = 0 // getNourishmentEventsAdjustment(definitions, deck, actor, events)
	val healthAccumulator = if (destructible.drainDuration != 0)
		result.healthAccumulator - getResourceTimeCost(healthTimeDrainDuration, 1)
	else
		result.healthAccumulator

	val healthAccumulation = getRoundedAccumulation(healthAccumulator)
	val mod = healthAccumulation + nourishmentAdjustment

	val lastDamage = prioritizeDamageSourceAttribution(damages)

	// IntelliJ is trying to convert these two blocks to use elvis but that isn't the same logic.
	// If the user takes damage from an unspecified source that should nullify the last non-null damage source,
	// not fall back to the last non-null damage source
	val lastDamageSource = if (lastDamage != null)
		lastDamage.source
	else
		destructible.lastDamageSource

	val lastDamageLocation = if (lastDamage != null)
		lastDamage.sourceLocation
	else
		destructible.lastDamageLocation

	return result.copy(
		health = modifyDestructible(mods, result, mod),
		healthAccumulator = healthAccumulator - healthAccumulation * highIntScale,
		lastDamageSource = lastDamageSource,
		lastDamageLocation = lastDamageLocation,
	)
}
