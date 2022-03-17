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

enum class MatchFrequency {
	essential, // Not hooked up yet
	greedy, // Matched if possible but not essential. Can exceed block count limits.
	normal,
	minimal, // Not hooked up yet, but will only be used as a last resort
}

data class Side(
	val mine: String,
	val other: Set<String>,
	val frequency: MatchFrequency = MatchFrequency.normal,

	// An integer between 0 and 99 that roughly indicates a reduced chance that this side will be filled.
	// Is only considered when frequency is normal.
	val rerollChance: Int = 0,
	val isTraversable: Boolean = true, // Cache value
)

// This is a solution for fringe cases and may end up being temporary
const val unmatchable = "unmatchable"
val unmatchableSide = Side(
	mine = unmatchable,
	other = setOf(),
)
