package compuquest.generation.general

val verticalSides = setOf(Direction.up, Direction.down)

// The standard 4 heights are incremental quarters + 10 for floor height padding
object StandardHeights {
  val first = 100
  val firstB = 225
  val second = 350
  val third = 600
  val fourth = 850
}

data class Side(
  val mine: String,
  val other: Set<String>,
  val height: Int = StandardHeights.first,
  val isTraversable: Boolean = true,
  val isEssential: Boolean = false, // This side needs to be connected to another side
  val canMatchEssential: Boolean = true,

    // Match this side if possible, ignoring block limits.
    // Unlike `isEssential`, a match is not required.
    // During the greedy pass, only new blocks without greedy sides are considered in order to prevent
    // infinite loops and excess greed.
  val isGreedy: Boolean = false,

    // Can match sides with different heights if the connection is nonessential
  val looseNonEssentialHeights: Boolean = false,
)

// This is a solution for fringe cases and may end up being temporary
const val unmatchable = "unmatchable"
val unmatchableSide = Side(
    mine = unmatchable,
    other = setOf(),
)
