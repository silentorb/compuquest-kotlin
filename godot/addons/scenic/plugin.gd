tool
extends EditorPlugin

const change_node_scene = "Change Node Scene"

func _enter_tree():
	add_tool_menu_item(change_node_scene, self, "on_change_node_scene")


func _exit_tree():
	remove_tool_menu_item(change_node_scene)


func change_node_type(node: Node):
	pass
	
func on_scene_selected(scene):
	pass

func on_change_node_scene(event):
	print("Hello World!")
	var scene_root = get_editor_interface().get_edited_scene_root()
	var scene_tree = scene_root.get_tree()
	var selection = get_editor_interface().get_selection()
	var nodes = selection.get_selected_nodes()
	var dialog = FileDialog.new()
	dialog.window_title = "Select a Scene"
	dialog.access = FileDialog.ACCESS_RESOURCES
	dialog.mode = FileDialog.MODE_OPEN_FILE
	dialog.add_filter("*.tscn, *.scn; Scenes")
	add_child(dialog)
	dialog.popup_centered_ratio()
	if nodes.size() == 0:
		return
	
#	selection.clear()
	var new_nodes = []
#	for node in nodes:
#		change_node_type(node)
	
