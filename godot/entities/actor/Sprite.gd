tool
extends Spatial

export(String) var animation setget animationSet

func animationSet(value):
	animation = value
	updateAnimation()

export(int) var frame setget frameSet

func frameSet(value):
	frame = value
	updateAnimation()

func updateAnimation():
	if (animation != ""):
		var sprite = find_node("sprite3d")
		sprite.animation = animation

func _ready():
	updateAnimation()
