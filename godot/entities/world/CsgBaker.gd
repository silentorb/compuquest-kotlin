extends CSGShape

func _ready():
	var k = is_root_shape()
	var old_name = name
	_update_shape()
	var meshes = get_meshes()
	name = name + "_csg"
	var mesh_instance = MeshInstance.new()
	mesh_instance.name = old_name
	mesh_instance.mesh = meshes[1]
	mesh_instance.transform = meshes[0]
	mesh_instance.material_override = get_parent().call_deferred("add_child", mesh_instance)
	queue_free()
