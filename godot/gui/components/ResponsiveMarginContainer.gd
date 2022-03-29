tool
extends Container

export var left = 0 setget set_left
export var top = 0 setget set_top
export var right = 0 setget set_right
export var bottom = 0 setget set_bottom

export(String, "parent,screen") var scaling_context = "parent" setget set_scaling_context

func set_left(value):
	if left != value:
		left = value
		resize_children()
		minimum_size_changed()

func set_top(value):
	if top != value:
		top = value
		resize_children()
		minimum_size_changed()

func set_right(value):
	if right != value:
		right = value
		resize_children()
		minimum_size_changed()

func set_bottom(value):
	if bottom != value:
		bottom = value
		resize_children()
		minimum_size_changed()

func set_scaling_context(value):
	if scaling_context != value:
		scaling_context = value
		resize_children()
		minimum_size_changed()

class Margins:
	var left
	var top
	var right
	var bottom
	
	func _init(left, top, right, bottom):
		self.left = left
		self.top = top
		self.right = right
		self.bottom = bottom

func get_context_size():
	match scaling_context:
		"parent":
			return get_size()
		"screen":
			var screen = get_viewport().size
			if screen.x < screen.y:
				return Vector2(screen.x, screen.x)
			else:
				return Vector2(screen.y, screen.y)

func get_resolved_margin():
	var size = get_context_size()
	return Margins.new(left * size.x / 100, top * size.y / 100, right * size.x / 100, bottom * size.y / 100)

func _get_minimum_size():
	var result = Vector2()
	
	for i in range(0, get_child_count()):
		var child = get_child(i) as Control
		if child == null || child.is_set_as_toplevel():
			continue
		
		if !child.is_visible_in_tree():
			continue
		
		var size = child.get_combined_minimum_size()
		if size.x > result.x:
			result.x = size.x
		
		if size.y > result.y:
			result.y = size.y
	
	var margins = get_resolved_margin()
	result.x += margins.left + margins.right
	result.y += margins.top + margins.bottom
	
	return result

func resize_children():
	var size = get_size()

	for i in range(0, get_child_count()):
		var child = get_child(i) as Control
		if child == null || child.is_set_as_toplevel():
			continue
		
		var margins = get_resolved_margin()
		var w = size.x - margins.left - margins.right
		var h = size.y - margins.top - margins.bottom
		fit_child_in_rect(child, Rect2(margins.left, margins.top, w, h))

func _notification(what):
	match what:
		NOTIFICATION_SORT_CHILDREN:			
			resize_children()
		
		NOTIFICATION_THEME_CHANGED:
			minimum_size_changed()
