tool
extends Node

export(Vector3) var cell = Vector3() setget set_cell
export(String, "up", "down", "east", "north", "west", "south") var direction = "east" setget set_direction

func set_cell(value):
	cell = value
	update_name()

func set_direction(value):
	direction = value
	update_name()

func update_name():
	var new_name = "%d, %d, %d - %s" % [cell.x, cell.y, cell.z, direction]
	if name != new_name:
		name = new_name
