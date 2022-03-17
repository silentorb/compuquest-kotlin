package compuquest.definition

import silentorb.mythic.ent.reflectProperties

object Sides {
	val closed = "closed"
	val slopeLeft = "slopeLeft"
	val slopeRight = "slopeRight"
	val space = "space"
	val traversable = "traversable"
}

val traversableSides = setOf(
	Sides.slopeLeft,
	Sides.slopeRight,
	Sides.traversable,
)

val baseSides = setOf(
	Sides.closed,
	Sides.slopeLeft,
	Sides.slopeRight
)

val sideGroups: Map<String, Set<String>> = mapOf(
	"any" to reflectProperties<String>(Sides).toSet(),
	"slopeLeftFlexible" to setOf(Sides.slopeLeft, Sides.space, Sides.closed),
	"slopeRightFlexible" to setOf(Sides.slopeRight, Sides.space, Sides.closed),
)
	
