tool
extends EditorScenePostImport

const meshScript = preload("res://assets/meshes/Mesh.gd")

func post_import(scene):
	var mesh = scene.get_child(0)
	scene.remove_child(mesh)
	scene.replace_by(mesh)

	mesh.set_script(meshScript)
	return mesh
