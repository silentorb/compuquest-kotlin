tool
extends Node

export(String) var type

var lastDepiction

var lastFrame = 0

func updateSprite():
	var sprite = get_parent().find_node("sprite")
	if sprite != null:
		sprite.animation = lastDepiction
		sprite.frame = lastFrame

func _ready():
	add_to_group("component")
	lastDepiction = Global.getCharacterDepiction(type)
	lastFrame = Global.getCharacterFrame(type)
	if Engine.editor_hint:
		updateSprite()

func _process(delta):
	if Engine.editor_hint:
		var depiction = Global.getCharacterDepiction(type)
		if lastDepiction != depiction:
			lastDepiction = depiction
			lastFrame = Global.getCharacterFrame(type)
