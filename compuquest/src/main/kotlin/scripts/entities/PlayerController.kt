package scripts.entities

import godot.AnimatedSprite3D
import godot.Camera
import godot.Node
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.NodePath
import godot.core.Transform
import scripts.Global

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

		val client = Global.instance!!.client!!
		val playerIndex = client.players[body.actor] ?: 0

		val sprite = body.findNode("sprite") as AnimatedSprite3D

		sprite.setLayerMaskBit(0L, false)
		for (i in 0..3) {
			sprite.setLayerMaskBit((i + 1).toLong(), i != playerIndex)
		}

		camera?.setCullMaskBit((playerIndex + 1).toLong(), false)
	}
}
