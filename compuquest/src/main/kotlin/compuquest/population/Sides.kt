package compuquest.population

enum class Sides {
	closed,
	slopeLeft,
	slopeRight,
	space,
	traversable,
}

val traversableSides = setOf(
	Sides.slopeLeft,
	Sides.slopeRight,
	Sides.traversable,
)
	.map { it.toString() }

//val sideGroups: Map<String, Set<String>> = mapOf(
//	"anyOpen" to anyOpen,
//	"anyOpenOrClosed" to anyOpen + BlockSides.closed,
//	"streetOrIntersection" to setOf(BlockSides.street, BlockSides.streetIntersection),
//)
