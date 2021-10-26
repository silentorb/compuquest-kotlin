extends ViewportContainer

func _process(delta):
	var child = get_child(0)
	child.size = rect_size
