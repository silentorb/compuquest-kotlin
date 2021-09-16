tool
extends AnimatedSprite

var dimensions

func updateTransform(nextDimensions):
	dimensions = nextDimensions
	var smallest = min(dimensions.x, dimensions.y)
	var _scale = smallest / 16.0
	position = dimensions / 2
	scale = Vector2(_scale, _scale)

func parentDimensions():
	return get_parent().rect_size

func _ready():
	updateTransform(parentDimensions())

func _process(delta):
	var nextDimensions = parentDimensions()
	var p = get_parent().get_parent()
	if (nextDimensions != dimensions):
	  updateTransform(nextDimensions)
	


