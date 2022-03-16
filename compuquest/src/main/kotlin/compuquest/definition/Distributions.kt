package compuquest.definition

import compuquest.generation.general.Rarity
import silentorb.mythic.debugging.getDebugInt
import silentorb.mythic.debugging.getDebugString

enum class DistributionGroup {
  cloud,
  victoryKey,
  merchant,
  monster,
  none,
  treasureChest
}

typealias DistributionMap = Map<DistributionGroup, Int>

fun monsterDistributions(): Map<String, Rarity> =
    if (getDebugString("MONSTER_TYPE") != null)
      mapOf(
          getDebugString("MONSTER_TYPE")!! to Rarity.common
      )
    else
      mapOf(
        Characters.skeleton to Rarity.common,
        Characters.skeletonSage to Rarity.uncommon,
      )

fun scalingDistributions(): DistributionMap = mapOf(
    DistributionGroup.none to 25,
    DistributionGroup.monster to 2
)

fun monsterLimit() = getDebugInt("MONSTER_LIMIT") ?: 1000

val distributedItems = mapOf(
  "res://entities/actor/BurgerBot.tscn" to Rarity.common,
)
