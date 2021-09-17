extends Node

func _ready():
	if !Engine.editor_hint:
		queue_free()
