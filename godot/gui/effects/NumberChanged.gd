extends Label

var original
# Need to defer layout initialization because the original label
# may not be properly positioned yet
var xSet = false
var alpha = 1.0
var color

func _process(delta):
	if !xSet:
		rect_position = original.rect_global_position
		xSet = true
	rect_position.y -= 40 * delta
	alpha -= 0.6 * delta
	add_color_override("font_color", Color(color.r, color.g, color.b, alpha))
	if alpha <= 0.0:
		queue_free()
