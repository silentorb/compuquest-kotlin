package compuquest.generation.general

import godot.core.Vector2
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.randomly.Dice
import silentorb.mythic.randomly.getAliasedIndex
import silentorb.mythic.randomly.newWeightedPoolTable
import silentorb.mythic.spatial.Vector2i
import silentorb.mythic.spatial.Vector3i
import silentorb.mythic.spatial.nearestFast2d
import silentorb.mythic.spatial.xz
import kotlin.math.max

typealias BiomeGrid = (Vector3i) -> String
typealias VoronoiAnchors2d<T> = List<VoronoiAnchor2d<T>>

fun getNeighboringBiomeWeights(biomes: Set<String>, aggregator: Map<Vector2i, String>, x: Int, y: Int): Map<String, Int> {
  val neighbourBiomes = aggregator.filterKeys {
    it.x >= x - 1 && it.x <= x + 1 &&
        it.y >= y - 1 && it.y <= y + 1
  }
      .entries
      .groupBy { it.value }
      .mapValues { it.value.size }

  return biomes
      .associateWith { 8 - (neighbourBiomes[it] ?: 0) }
}

fun selectBiome(dice: Dice, weights: Map<String, Int>): String {
  val pool = newWeightedPoolTable(weights.values)
  val index = getAliasedIndex(pool, dice)
  return weights.keys.toList()[index]
}

tailrec fun addBiomeAnchor(biomes: Set<String>, dice: Dice, anchorCount: Int, gridLength: Int, step: Int = 0,
                           aggregator: Map<Vector2i, String> = mapOf()): Map<Vector2i, String> =
    if (step >= anchorCount)
      aggregator
    else {
      val y = step / gridLength
      val x = step - y * gridLength
      val weights = getNeighboringBiomeWeights(biomes, aggregator, x, y)
      val biome = selectBiome(dice, weights)
      val next = aggregator.plus(Vector2i(x, y) to biome)
      addBiomeAnchor(biomes, dice, anchorCount, gridLength, step + 1, next)
    }

fun newBiomeAnchors(biomes: Set<String>, dice: Dice, worldRadius: Float, biomeSize: Float, minGap: Float): VoronoiAnchors2d<String> {
  val biomeLengthCount = max(1, (worldRadius / biomeSize).toInt())
  val margin = minGap / 4f
  val randomRange = biomeSize - minGap
  val startOffset = -biomeSize * biomeLengthCount / 2f
  val anchorBiomes = addBiomeAnchor(biomes, dice, biomeLengthCount * biomeLengthCount, biomeLengthCount)
  val anchors = anchorBiomes.map { (cell, biome) ->
    VoronoiAnchor2d(
        position = Vector2(
            startOffset + cell.x * biomeSize + margin + dice.getFloat(0f, randomRange),
            startOffset + cell.y * biomeSize + margin + dice.getFloat(0f, randomRange),
        ),
        value = biome,
    )
  }
//  val anchors = (0 until biomeLengthCount).flatMap { y ->
//    (0 until biomeLengthCount).map { x ->
//      VoronoiAnchor2d(
//          position = Vector2(
//              startOffset + x * biomeSize + margin + dice.getFloat(0f, randomRange),
//              startOffset + y * biomeSize + margin + dice.getFloat(0f, randomRange),
//          ),
//          value = dice.takeOne(biomes)
//      )
//    }
//  }
  if (getDebugBoolean("DEBUG_BIOME_GRID")) {
    (0 until biomeLengthCount).forEach { y ->
      val line = anchorBiomes
          .filterKeys { it.y == y }
          .entries
          .sortedBy { it.key.x }
          .joinToString(" ") { it.value.substring(0, 2) }

      println(line)
    }
  }
  return anchors
}

fun biomeGridFromAnchors(anchors: VoronoiAnchors2d<String>): BiomeGrid {
  val items = anchors.map { Pair(it.position, it.value) }
  val nearest = nearestFast2d(items)
  return { cell ->
    nearest(absoluteCellPosition(cell).xz())
  }
}

//private fun logGrid(grid: Grid<BiomeId>, boundary: WorldBoundary) {
//  for (y in 0 until (boundary.dimensions.y).toInt()) {
//    val line = (0 until (boundary.dimensions.x).toInt()).map { x ->
//      grid(
//          x.toFloat() - boundary.dimensions.x / 2f,
//          y.toFloat() - boundary.dimensions.y / 2f)
//          .ordinal
//    }
//        .joinToString(" ")
//    println(line)
//  }
//}

