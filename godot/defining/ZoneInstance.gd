tool
extends Spatial

export(PackedScene) var scene setget setScene

export(String, "microcorp", "neutral", "undead") var faction = "neutral"

export(bool) var active = true setget setActive

# For debug purposes
export(bool) var populate = true

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
