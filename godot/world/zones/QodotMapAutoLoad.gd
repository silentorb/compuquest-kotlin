extends Node
tool

# This script will auto-full-build a Qodot map whenever it's source file
# is modified

# To use, attach this script to a node (the auto-builder node) in the scene with your Qodot map
# and set the auto-builder node's map property to be the Qodot map node

# This script is only intended for live editing sessions and will only
# run when the scene with this script is active in the editor

export (NodePath) var map
export (int) var frequency = 60 * 2 # Defaults to 2 seconds
export (int) var last_modified = 0 # Not meant to be set by the user

onready var _map: QodotMap = get_node(map)
var step = 0
var delay = 0
func _ready():
	if not Engine.is_editor_hint():
		queue_free()

func _physics_process(delta):
	if Engine.is_editor_hint() && _map != null:
		step += 1
		if delay > 0:
			delay += 1
			if delay > 60 * 4:
				delay = 0
				_map.should_add_children = true
				_map.should_set_owners = true
				_map.verify_and_build()				
		elsif step > 60 * 2:
			step = 0
			var resource_path = _map.map_file
			# Ideally this would be checking the .import file hashes but
			# I haven't found a means through the GDScript API to determine
			# The associated .import files for a resource
			var modified = File.new().get_modified_time(resource_path)
			
			if last_modified != modified:
				print('Rebuilding modified map...')
				last_modified = modified
				delay = 1
