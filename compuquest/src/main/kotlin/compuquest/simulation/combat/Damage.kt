package compuquest.simulation.combat

import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.Deck
import compuquest.simulation.general.getAccessoriesSequence
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

const val damageCommand = "damage"

fun applyDamage(deck: Deck, actor: Id, characterEvents: Events): Int {
  val damages = filterEventValues<Int>(damageCommand, characterEvents)
  return if (damages.any()) {
    val damageReduction = getAccessoriesSequence(deck.accessories, actor)
      .filter { it.value.definition.effect == AccessoryEffects.damageReduction }
      .sumBy { it.value.definition.strengthInt }

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
  damages
    .map(applyDamageMods(multipliers))
    .sum()

fun aggregateHealthModifiers(destructible: Destructible, damages: List<Damage>): Int {
  val damage = aggregateDamage(destructible.damageMultipliers, damages)
  return -damage
}

typealias DamageModifierQuery = (Id) -> (DamageType) -> List<Int>

fun calculateDamageMultipliers(damageTypes: Set<DamageType>, modifierQuery: DamageModifierQuery, id: Id, base: DamageMultipliers): DamageMultipliers {
  val query = modifierQuery(id)
  return damageTypes.map { damageType ->
    val baseMultiplier = base[damageType] ?: defaultDamageMultiplier
    val modifiers = query(damageType)
    val aggregate = modifiers.sum()
    val value = baseMultiplier + aggregate
    Pair(damageType, value)
  }.associate { it }
}
