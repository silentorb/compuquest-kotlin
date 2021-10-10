tool
extends EditorScenePostImport

const meshScript = preload("res://assets/meshes/scripts/Mesh.gd")

func post_import(scene):
	print("scene " + scene.name)
	for child in scene.get_children():
		print("  " + child.name + " " + child.filename)
		child.set_script(meshScript)
	return scene
