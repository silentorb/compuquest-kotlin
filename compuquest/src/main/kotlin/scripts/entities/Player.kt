package scripts.entities

import godot.*
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.NodePath
import godot.core.Vector2
import godot.core.Vector3
import godot.global.GD
import kotlin.math.abs

@RegisterClass
class Player : KinematicBody() {

	@Export
	@RegisterProperty
	var mouseSensitivity: Float = 8f

	@Export
	@RegisterProperty
	var headPath: NodePath? = null

	@Export
	@RegisterProperty
	var cameraPath: NodePath? = null

	@Export
	@RegisterProperty
	var fov: Float = 80f

	var mouseAxis = Vector2.ZERO
	var head: Spatial? = null
	var camera: Camera? = null
	var snap: Vector3 = Vector3.ZERO
	var velocity = Vector3.ZERO
	var moveAxis = Vector3.ZERO

	companion object {
		val floorMaxAngle = GD.deg2rad(46f)
	}

	@Export
	@RegisterProperty
	var gravity: Float = 30f

	@Export
	@RegisterProperty
	var walkSpeed: Float = 10f

	@Export
	@RegisterProperty
	var acceleration: Float = 8f

	@Export
	@RegisterProperty
	var deacceleration: Float = 10f

	@Export
	@RegisterProperty
	var airControl: Float = 0.3f

	@Export
	@RegisterProperty
	var jumpHeight: Float = 10f

	var speed: Float = 0f
	var isJumpingInput = false
	var isActive = true

	@RegisterFunction
	override fun _ready() {
		head = getNode(headPath!!) as? Spatial
		camera = getNode(cameraPath!!) as? Camera
		camera!!.fov = fov.toDouble()
		speed = walkSpeed
	}

	fun directionInput(): Vector3 {
		val aim = globalTransform.basis
		val directionA = when {
			moveAxis.x >= 0.5f -> -aim.z
			moveAxis.x <= -0.5f -> aim.z
			else -> Vector3.ZERO
		}

		val directionB = when {
			moveAxis.y <= -0.5f -> -aim.x
			moveAxis.y >= 0.5f -> aim.x
			else -> Vector3.ZERO
		}

		val result = directionA + directionB
		return Vector3(result.x, 0f, result.z).normalized()
	}

	fun accelerate(delta: Float) {
		val direction = directionInput()
		var tempVelocity = velocity
		val target = direction * speed

		tempVelocity.y = 0.0
		val tempAcceleration = when {
			direction.dot(tempVelocity) > 0f -> acceleration
			else -> deacceleration
		} * if (!isOnFloor()) airControl else 1f

		tempVelocity = tempVelocity.linearInterpolate(target, (tempAcceleration * delta).toDouble())

		velocity.x = tempVelocity.x
		velocity.z = tempVelocity.z

		if (direction.dot(velocity) == 0.0) {
			val velocityClamp = 0.01f
			if (abs(velocity.x) < velocityClamp)
				velocity.x = 0.0
			if (abs(velocity.z) < velocityClamp)
				velocity.z = 0.0
		}
	}

	fun jump() {
		if (isJumpingInput) {
			velocity.y = jumpHeight.toDouble()
			snap = Vector3.ZERO
		}
	}

	fun updateSpeed() {
		if (speed < walkSpeed) {
			speed = GD.lerp(speed, walkSpeed, 0.05f)
		}
	}

	fun walk(delta: Float) {
		if (isOnFloor()) {
			snap = -getFloorNormal() - getFloorVelocity() * delta

			if (velocity.y < 0f)
				velocity.y = 0.0

			jump()
		} else {
			if (snap != Vector3.ZERO && velocity.y != 0.0) {
				velocity.y = 0.0
			}

			snap = Vector3.ZERO
			velocity.y = (gravity * delta).toDouble()
		}

		updateSpeed()

		accelerate(delta)

		var newVelocity = moveAndSlideWithSnap(velocity, snap, Vector3.UP, true, 4, floorMaxAngle)
		isJumpingInput = false
	}

	fun cameraRotation() {
		val horizontal = -mouseAxis.x * mouseSensitivity / 100
		val vertical = -mouseAxis.y * mouseSensitivity / 100

		mouseAxis = Vector2.ZERO

		rotateY(GD.deg2rad(horizontal))
		head!!.rotateX(GD.deg2rad(vertical))

		val tempRotation = head!!.rotationDegrees
		tempRotation.x = GD.clamp(tempRotation.x, -90, 90)
		head!!.rotationDegrees = tempRotation
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		if (isActive) {
			Input.setMouseMode(Input.MOUSE_MODE_CAPTURED)
			moveAxis.x = Input.getActionStrength("move_forward") - Input.getActionStrength("move_backward")
			moveAxis.y = Input.getActionStrength("move_right") - Input.getActionStrength("move_left")

			if (Input.isActionJustPressed("move_jump")) {
				isJumpingInput = true
			}
		} else {
			Input.setMouseMode(Input.MOUSE_MODE_VISIBLE)
		}
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		if (isActive) {
			walk(delta.toFloat())
		}
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		if (isActive) {
			if (event is InputEventMouseMotion) {
				mouseAxis = event.relative
				cameraRotation()
			}
		}
	}
}
