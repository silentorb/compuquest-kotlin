package scripts.entities

import compuquest.simulation.general.isPlayerDead
import godot.*
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.NodePath
import godot.core.Vector2
import godot.core.Vector3
import godot.global.GD
import scripts.Global

@RegisterClass
class PlayerController : Node() {

	@Export
	@RegisterProperty
	var headPath: NodePath? = null

	@Export
	@RegisterProperty
	var mouseSensitivity: Float = 8f

	@Export
	@RegisterProperty
	var cameraPath: NodePath? = null

	var camera: Camera? = null

	@Export
	@RegisterProperty
	var fov: Float = 80f

	var mouseAxis = Vector2.ZERO
	var moveAxis = Vector3.ZERO

	@RegisterFunction
	override fun _ready() {
		val body = getParent() as CharacterBody
		body.head = getNode(headPath!!) as? Spatial
		camera = getNode(cameraPath!!) as? Camera
		camera!!.fov = fov.toDouble()
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		val body = getParent() as? CharacterBody
		if (body != null) {
			if (body.isActive) {
				Input.setMouseMode(Input.MOUSE_MODE_CAPTURED)
				moveAxis.x = Input.getActionStrength("move_forward") - Input.getActionStrength("move_backward")
				moveAxis.y = Input.getActionStrength("move_right") - Input.getActionStrength("move_left")

				if (Input.isActionJustPressed("move_jump")) {
					body.isJumpingInput = true
				}
			} else {
				moveAxis = Vector3.ZERO
				body.isJumpingInput = false
				Input.setMouseMode(Input.MOUSE_MODE_VISIBLE)
			}
			body.moveDirection = body.directionInput(moveAxis)
		}
	}

	fun cameraRotation(body: CharacterBody) {
		val horizontal = -mouseAxis.x * mouseSensitivity / 100
		val vertical = -mouseAxis.y * mouseSensitivity / 100

		mouseAxis = Vector2.ZERO

		body.rotateY(GD.deg2rad(horizontal))
		body.head!!.rotateX(GD.deg2rad(vertical))

		val tempRotation = body.head!!.rotationDegrees
		tempRotation.x = GD.clamp(tempRotation.x, -90, 90)
		body.head!!.rotationDegrees = tempRotation
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		val body = getParent() as? CharacterBody
		if (body != null && body.isActive) {
			if (event is InputEventMouseMotion) {
				mouseAxis = event.relative
				cameraRotation(body)
			}
		}
	}

	fun deathCollapse(head: Spatial) {
		head.translation {
			y = GD.lerp(head.translation.y.toFloat(), 0.15f, 0.05f).toDouble()
		}

		head.rotation {
			z = GD.lerp(head.rotation.z.toFloat(), 0.5f, 0.05f).toDouble()
		}
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		val body = getParent() as? CharacterBody
		if (body != null) {
			if (isPlayerDead(Global.world?.deck)) {
				deathCollapse(body.head!!)
			}
		}
	}
}
