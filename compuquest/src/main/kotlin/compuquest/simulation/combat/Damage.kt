package compuquest.simulation.combat

import compuquest.simulation.characters.getAccessoriesSequence
import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.Deck
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues
import kotlin.math.max

const val defaultDamageMultiplier = 100
typealias DamageMultipliers = Map<DamageType, Percentage>

data class Damage(
  val type: DamageType,
  val amount: Int,
  val source: Id
)

const val damageEvent = "damage"

fun applyDamage(deck: Deck, actor: Id, characterEvents: Events): Int {
  val damages = filterEventValues<Int>(damageEvent, characterEvents)
  return if (damages.any()) {
    val damageReduction = getAccessoriesSequence(deck.accessories, actor)
      .sumOf { (_, value) ->
        value.definition.effects
          .filter { it.type == AccessoryEffects.damageReduction }
          .sumOf { it.strengthInt }
      }

    damages.sumOf { -max(0, it - damageReduction) }
  } else
    0
}

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
