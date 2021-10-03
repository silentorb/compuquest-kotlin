tool
extends Node

export(Resource) var creature

export(String) var faction

export(int) var healthValue

export(bool) var includeFees = true

var lastDepiction

func updateSprite():
	var sprite = get_parent().find_node("sprite")
	if sprite != null:
		sprite.animation = lastDepiction

func _ready():
	add_to_group("component")
	lastDepiction = creature.depiction
	if Engine.editor_hint:
		updateSprite()

func _process(delta):
	if Engine.editor_hint and lastDepiction != creature.depiction:
		lastDepiction = creature.depiction
