package compuquest.simulation.combat

import compuquest.simulation.characters.*
import compuquest.simulation.definition.ResourceTypes
import compuquest.simulation.general.highIntScale
import compuquest.simulation.general.modifyResource
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventTargets
import silentorb.mythic.happening.filterEventsByType

data class Destructible(
	val health: Int,
	val maxHealth: Int,
	val healthAccumulator: Int = 0,
	val drainDuration: Int = 0,
	val damageMultipliers: DamageMultipliers = mapOf(),
	val lastDamageSource: Id = 0
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

fun modifyDestructible(events: Events, actor: Id, destructible: Destructible, mod: Int = 0) =
	modifyResourceWithEvents(events, actor, ResourceTypes.health, destructible.health, destructible.maxHealth, mod)

fun updateDestructible(events: Events): (Id, Destructible) -> Destructible {
	val damageEvents = filterEventsByType<DamageEvent>(damageEvent, events)
	val restoreEvents = filterEventTargets<Long>(restoreFullHealthEvent, events)

	return { actor, destructible ->
		val result = if (restoreEvents.contains(actor))
			restoreFullHealth(destructible)
		else {
			val damages =
				damageEvents
					.filter { it.target == actor }
					.map { it.value.damage }

			damageDestructible(damages)(destructible)
		}
		val nourishmentAdjustment = 0 // getNourishmentEventsAdjustment(definitions, deck, actor, events)
		val healthAccumulator = if (destructible.drainDuration != 0)
			result.healthAccumulator - getResourceTimeCost(healthTimeDrainDuration, 1)
		else
			result.healthAccumulator

		val healthAccumulation = getRoundedAccumulation(healthAccumulator)
		val mod = healthAccumulation + nourishmentAdjustment
		result.copy(
			health = modifyDestructible(events, actor, result, mod),
			healthAccumulator = healthAccumulator - healthAccumulation * highIntScale,
		)
	}
}
