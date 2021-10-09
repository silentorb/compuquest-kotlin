tool
extends MeshInstance

export(Resource) var texture setget set_texture
const materialResource = preload("res://assets/materials/texture.tres")

func set_texture(value):
	texture = value	
	var material = get_surface_material(0)	
	if material == null:
		material = materialResource.duplicate()
		set_surface_material(0, material)
	material.albedo_texture = value
