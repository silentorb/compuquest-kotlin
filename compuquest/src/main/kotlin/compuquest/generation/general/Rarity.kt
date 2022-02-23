package compuquest.generation.general

import silentorb.mythic.ent.mappedCache
import silentorb.mythic.randomly.WeightedPoolTable
import silentorb.mythic.randomly.newWeightedPoolTable

enum class Rarity(val probability: Int) {
  common(55),
  uncommon(34),
  rare(10),
  legendary(1),
}

val newRarityTable = mappedCache<Collection<Rarity>, WeightedPoolTable> { pool ->
  newWeightedPoolTable(pool.map { it.probability })
}
