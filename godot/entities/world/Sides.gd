tool
extends Node

var base = PoolStringArray([
	"closed",	
	"slopeLeft",
	"slopeRight",
	"space",
	"traversable",
	"verticalDiagonalSpace"
]).join(",")

var all = base + "," + PoolStringArray([
	"any",
	"slopeLeftFlexible",
	"slopeRightFlexible"
]).join(",")
