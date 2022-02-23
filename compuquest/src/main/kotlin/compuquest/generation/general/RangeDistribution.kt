package compuquest.generation.general

import silentorb.mythic.randomly.Dice

// This assumes that there is no entry with a weight of zero
fun <T> normalizeRangesFallback(slotCount: Int, ranges: Map<T, Int>): Map<T, Int> {
  val sorted = ranges.entries
      .sortedByDescending { it.value }

  val result = mutableMapOf<T, Int>()
  var remainingCount = slotCount
  for (i in sorted.indices) {
    if (remainingCount == 0)
      break

    val entry = sorted[i]
    val weight = entry.value
    val nextWeight = if (i < sorted.size - 1) sorted[i + 1].value else 0
    val count = Math.min(remainingCount, weight / nextWeight)
    result[entry.key] = count
    remainingCount -= count
  }
  return result
//  return sorted
//      .take(slotCount)
//      .associate { Pair(it.key, 1) }
}

// This supports entries with a weight of zero (they are filtered out)
fun <T> normalizeRanges(slotCount: Int, ranges: Map<T, Int>): Map<T, Int> {
  val filtered = ranges.filter { it.value > 0 }
  if (slotCount < filtered.size)
    return normalizeRangesFallback(slotCount, filtered)

  val rangeTotal = filtered.values.sum()
  val initialNormalized = filtered.mapValues { it.value * slotCount / rangeTotal }
  val initialNormalizedTotal = initialNormalized.values.sum()
  val gap = slotCount - initialNormalizedTotal
  assert(gap >= 0)
  assert(gap <= filtered.size)
  val finalNormalized = if (gap > 0)
    initialNormalized.plus(
        initialNormalized.entries
            .sortedByDescending { it.value }
            .take(gap)
            .map { Pair(it.key, it.value + 1) }
            .associate { it }
    )
  else
    initialNormalized

  val finalNormalizedTotal = finalNormalized.values.sum()
  assert(finalNormalizedTotal == slotCount)
  return finalNormalized
}

fun <T> distributeToSlots(dice: Dice, slotCount: Int, scaling: Map<T, Int>, fixed: Map<T, Int>): List<T> {
  val fixedSlotCount = fixed.entries.sumBy { it.value }
  val scalingSlotCount = slotCount - fixedSlotCount
  assert(scalingSlotCount > -1)
  val normalized = normalizeRanges(scalingSlotCount, scaling)
  val pool = normalized.flatMap { range ->
    (1..range.value).map { range.key }
  }
      .plus(fixed.flatMap { (key, count) ->
        (1..count).map { key }
      })
  return dice.shuffle(pool)
}

fun <T> distributeToRaritySlots(dice: Dice, slotCount: Int, scaling: Map<T, Rarity>): List<T> {
  val rarities = scaling.values
      .distinct()
      .sortedByDescending { it.probability }

  val rarityTotal = rarities.sumBy { it.probability }

  val distribution = rarities
      .flatMap { rarity ->
        val raritySize = slotCount * rarity.probability / rarityTotal
        val rarityPool = scaling.filter { it.value == rarity }.keys
        (0 until raritySize).map { dice.takeOne(rarityPool) }
      }
      .take(slotCount)

  return dice.shuffle(distribution)
}
