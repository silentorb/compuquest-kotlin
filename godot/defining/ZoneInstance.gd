tool
extends Spatial

export(PackedScene) var scene setget setScene

export(Resource) var faction

export(bool) var active = true setget setActive

func setScene(value):
	if scene != value:
		scene = value
		onSceneChanged()

func setActive(value):
	if active != value:
		active = value
		onActiveChanged()

func isActive():
	return !Engine.editor_hint or active

func clear():
	for child in get_children():
		child.queue_free()

func onSceneChanged():
	if isActive():
		clear()
		if scene != null:
			add_child(scene.instance())

func onActiveChanged():
	if isActive():
		onSceneChanged()
	else:
		clear()

func _ready():
	add_to_group("zone")
