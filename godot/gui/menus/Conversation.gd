extends Control

func _ready():
	var back = find_node("back")
	back.connect("pressed", self, "onBack")

func onBack():
	queue_free()

