tool
extends Node

var base = PoolStringArray([
	"closed",
	"slopeLeft",
	"slopeRight",
	"space",
	"traversable",
]).join(",")

var all = base + "," + PoolStringArray([
	"any",
	"slopeLeftFlexible",
	"slopeRightFlexible"
]).join(",")
