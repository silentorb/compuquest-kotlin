extends Spatial

func _process(_delta):
	var camera = get_viewport().get_camera()
	if camera != null:
		var camera_pos = camera.global_transform.origin
		look_at(camera_pos, Vector3.UP)
