package compuquest.simulation.combat

import compuquest.simulation.characters.*
import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.ResourceTypes
import compuquest.simulation.general.Deck
import compuquest.simulation.general.highIntScale
import compuquest.simulation.general.modifyResource
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventsByType

data class DestructibleBaseStats(
    val health: Int,
    val damageMultipliers: DamageMultipliers = mapOf()
)

data class Destructible(
    val base: DestructibleBaseStats,
    val health: Int,
    val maxHealth: Int,
    val healthAccumulator: Int = 0,
    val drainDuration: Int = 0,
    val damageMultipliers: DamageMultipliers = mapOf(),
    val lastDamageSource: Id = 0
)

// This is intended to be used outside of combat.
// It overrides any other health modifying events.
data class RestoreHealth(
    val target: Id
)

fun updateDestructibleCache(damageTypes: Set<DamageType>, modifierQuery: DamageModifierQuery): (Id, Destructible) -> Destructible = { id, destructible ->
  val multiplers = calculateDamageMultipliers(damageTypes, modifierQuery, id, destructible.base.damageMultipliers)
  destructible.copy(
      damageMultipliers = multiplers
  )
}

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

val restoreDestructibleHealth: (Destructible) -> Destructible = { destructible ->
  destructible.copy(
      health = destructible.maxHealth
  )
}

fun modifyDestructible(events: Events, actor: Id, destructible: Destructible, mod: Int = 0) =
    modifyResourceWithEvents(events, actor, ResourceTypes.health, destructible.health, destructible.maxHealth, mod)

fun updateDestructibleHealth(definitions: Definitions, deck: Deck, events: Events): (Id, Destructible) -> Destructible {
  val damageEvents = filterEventsByType<DamageEvent>(damageEvent, events)
  val restoreEvents = events.filterIsInstance<RestoreHealth>()

  return { actor, destructible ->
    val result = if (restoreEvents.any { it.target == actor }) {
      restoreDestructibleHealth(destructible)
    } else {
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
