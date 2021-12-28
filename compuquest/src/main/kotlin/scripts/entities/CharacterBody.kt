package scripts.entities

import godot.KinematicBody
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector3
import godot.global.GD
import kotlin.math.abs

@RegisterClass
class CharacterBody : KinematicBody() {

	var head: Spatial? = null
	var snap: Vector3 = Vector3.ZERO
	var velocity = Vector3.ZERO
	var toolOffset: Vector3 = Vector3.ZERO

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
		speed = walkSpeed
		toolOffset = (findNode("toolOrigin") as? Spatial)?.translation ?: Vector3.ZERO
	}

	fun directionInput(moveAxis: Vector3): Vector3 {
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

	fun accelerate(direction: Vector3, delta: Float) {
		var tempVelocity = Vector3(velocity.x, 0.0, velocity.z)
		val target = direction * speed

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

	fun walk(direction: Vector3, delta: Float) {
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
			velocity.y -= (gravity * delta).toDouble()
		}

		updateSpeed()

		accelerate(direction, delta)

		var newVelocity = moveAndSlideWithSnap(velocity, snap, Vector3.UP, true, 4, floorMaxAngle)
		var k = isOnFloor()
		isJumpingInput = false
	}
}
