tool
extends EditorPlugin

const change_node_scene = "Change Node Scene"
const config_file_path = "res://.gd/ui.ini"
const change_node_type_section = "change_node_type"
const last_type = "last_type"
var config = ConfigFile.new()


func _enter_tree():
	load_config()
	add_tool_menu_item(change_node_scene, self, "on_change_node_scene")


func _exit_tree():
	remove_tool_menu_item(change_node_scene)


func load_config():
	config.load(config_file_path)


func save_config():
	Directory.new().make_dir("res://.gde")
	config.save(config_file_path)


func change_node_type(node: Node, scene: PackedScene):
	var parent = node.get_parent()
	var new_node = scene.instance()	
	new_node.name = node.name
	new_node.transform = node.transform
	var index = node.get_index()
	print("a")
	parent.remove_child(node)
	print("b")
	parent.add_child(new_node)
	print("c")
	new_node.set_owner(get_tree().get_edited_scene_root())
	parent.move_child(new_node, index)
	print("d")
	node.queue_free()
	print("finished")
	return new_node


func get_selection():
	var scene_root = get_editor_interface().get_edited_scene_root()
	var scene_tree = scene_root.get_tree()
	return get_editor_interface().get_selection()


func on_scene_selected(scene_path):
	config.set_value(change_node_type_section, last_type, scene_path)
	save_config()
	var selection = get_selection()
	var nodes = selection.get_selected_nodes()
	if nodes.size() == 0:
		return


	var scene = load(scene_path)
	
	var new_nodes = []
	for node in nodes:
		var new_node = change_node_type(node, scene)
		selection.add_node(new_node)
	

func on_change_node_scene(event):
	var selection = get_selection()
	if selection.get_selected_nodes().size() == 0:
		return
	
	var dialog = FileDialog.new()
	dialog.window_title = "Select a Scene"
	dialog.access = FileDialog.ACCESS_RESOURCES
	dialog.mode = FileDialog.MODE_OPEN_FILE
	dialog.add_filter("*.tscn, *.scn; Scenes")
	load_config()
	var current_file = config.get_value("change_node_type", last_type, "")
	dialog.current_dir = current_file.get_base_dir()
	dialog.current_file = current_file.get_file()
	dialog.connect("file_selected", self, "on_scene_selected")
	add_child(dialog)
	dialog.popup_centered_ratio()
