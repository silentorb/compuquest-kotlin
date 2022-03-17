tool
extends "res://entities/world/CellDirectionNode.gd"

export(int) var cellHeight = 1

var mine := "traversable"
var other := "traversable"
var frequency := "normal"
var rerollChance := 0

func _get_property_list():

	var properties = [
		{
			name = "mine",
			type = TYPE_STRING,
			hint = PROPERTY_HINT_ENUM,
			hint_string = Sides.base
		},
		{
			name = "other",
			type = TYPE_STRING,
			hint = PROPERTY_HINT_ENUM,
			hint_string = Sides.all
		},
		{
			name = "frequency",
			type = TYPE_STRING,
			hint = PROPERTY_HINT_ENUM,
			hint_string = "essential,greedy,normal,minimal"
		},
		{
			name = "rerollChance",
			type = TYPE_INT,
			hint_string = "0,100"
		}
	]
	
	return properties
