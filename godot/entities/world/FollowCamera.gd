extends Spatial

func _process(delta):
	var camera = get_viewport().get_camera()
	if camera != null:
		translation = camera.global_transform.origin

