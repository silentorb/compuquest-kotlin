package scripts.entities

import godot.*
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.NodePath
import godot.core.Transform
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId

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

	var actor: Id = emptyId

	var environment: Environment? = null
		set(value) {
			if (field != value) {
				field = value
				camera?.environment = value
			}
		}

	@RegisterFunction
	override fun _ready() {
		val body = getParent() as CharacterBody
		body.head = getNode(headPath!!) as? Spatial
		body.headRestingState = body.head?.transform ?: Transform.IDENTITY
		camera = getNode(cameraPath!!) as? Camera
		camera?.fov = fov.toDouble()

		val client = Global.instance!!.client!!
		actor = body.actor
		val playerIndex = client.playerMap[actor] ?: 0

		val playerSprite = (body as Node).findNode("sprite") as AnimatedSprite3D

		playerSprite.setLayerMaskBit(0L, false)
		for (i in 0..3) {
			playerSprite.setLayerMaskBit((i + 1).toLong(), i != playerIndex)
		}

		camera?.setCullMaskBit((playerIndex + 1).toLong(), false)
		body.playerController = this
	}
}
