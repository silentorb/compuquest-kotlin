package scripts.entities

import godot.Camera
import godot.Node
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.NodePath
import godot.core.Transform

@RegisterClass
class PlayerController : Node() {

	@Export
	@RegisterProperty
	var headPath: NodePath? = null

	@Export
	@RegisterProperty
	var cameraPath: NodePath? = null

	var camera: Camera? = null

	@Export
	@RegisterProperty
	var fov: Float = 80f

	@RegisterFunction
	override fun _ready() {
		val body = getParent() as CharacterBody
		body.head = getNode(headPath!!) as? Spatial
		body.headRestingState = body.head?.transform ?: Transform.IDENTITY
		camera = getNode(cameraPath!!) as? Camera
		camera?.fov = fov.toDouble()
	}
}
