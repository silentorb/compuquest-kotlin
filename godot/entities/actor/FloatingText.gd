extends Spatial

onready var label = $control/background/label
onready var control = $control

export var occlusion = false
export var text = "floating text" setget set_text, get_text

func set_text(value):
	text = value
	label.text = value

func get_text():
	return label.text

func _physics_process(_delta):
	var camera = get_viewport().get_camera()
	if camera != null and label != null && control != null:
		var location = global_transform.origin
		var camera_location = camera.global_transform.origin
		var occluded = false
		var initial_visible = not camera.is_position_behind(location) and location.distance_to(camera_location) < 10.0
		if occlusion and initial_visible:
			var space_state = get_world().direct_space_state
			var hit = space_state.intersect_ray(camera_location, location)
			occluded = hit
		
		control.visible = initial_visible and not occluded and not text.empty()
		if control.visible:
			control.rect_position = camera.unproject_position(location)
