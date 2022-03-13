tool
extends Node

export(bool) var isGreedy = false
export(bool) var isEssential = false
export(bool) var canMatchEssential = true
export(Vector3) var cell = Vector3() setget set_cell
export(String, "up", "down", "east", "north", "west", "south") var direction = "east" setget set_direction
export(int) var cellHeight = 1
var mine := "traversable"
var other := "traversable"

var sideOptions = PoolStringArray([
	"closed",
	"slopeLeft",
	"slopeRight",
	"space",
	"traversable",
]).join(",")

func set_cell(value):
	cell = value
	update_name()

func set_direction(value):
	direction = value
	update_name()

func update_name():
	name = "%d, %d, %d - %s" % [cell.x, cell.y, cell.z, direction]

func _get_property_list():
	var properties = [
		{
			name = "mine",
			type = TYPE_STRING,
			hint = PROPERTY_HINT_ENUM,
			hint_string = sideOptions
		},
			{
			name = "other",
			type = TYPE_STRING,
			hint = PROPERTY_HINT_ENUM,
			hint_string = sideOptions
		},
	]
	
	return properties
