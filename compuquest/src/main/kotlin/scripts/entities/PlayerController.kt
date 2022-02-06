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
	var equippedFrame: Int = -1
		set(value) {
			if (field != value) {
				val localSprite = equippedSprite
				if (localSprite != null) {
					if (value != -1) {
						localSprite.frame = value.toLong()
					}
					localSprite.visible = value != -1
				}
				field = value
			}
		}

	var equippedSprite: AnimatedSprite3D? = null

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

		val playerSprite = body.findNode("sprite") as AnimatedSprite3D
		equippedSprite = body.findNode("equipped") as AnimatedSprite3D

		playerSprite.setLayerMaskBit(0L, false)
		for (i in 0..3) {
			playerSprite.setLayerMaskBit((i + 1).toLong(), i != playerIndex)
		}

		camera?.setCullMaskBit((playerIndex + 1).toLong(), false)
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		val world = Global.world
		if (world != null) {
			val deck = world.deck
			val activeAccessory = deck.characters[actor]?.activeAccessory ?: emptyId
			if (activeAccessory != emptyId) {
				val accessory = deck.accessories[activeAccessory]
				if (accessory != null) {
					val nextEquippedFrame = accessory.definition.equippedFrame
					equippedFrame = nextEquippedFrame
				}
			} else {
				equippedFrame = -1
			}
		}
	}
}
