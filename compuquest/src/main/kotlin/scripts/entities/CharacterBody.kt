package scripts.entities

import compuquest.simulation.characters.Character
import compuquest.simulation.input.PlayerInput
import compuquest.simulation.physics.CollisionMasks
import godot.AnimatedSprite3D
import godot.CollisionShape
import godot.KinematicBody
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Transform
import godot.core.Vector3
import godot.global.GD
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId
import silentorb.mythic.godoting.getCollisionShapeRadius
import kotlin.math.abs

@RegisterClass
class CharacterBody : KinematicBody() {

	var head: Spatial? = null
	var snap: Vector3 = Vector3.ZERO
	var velocity = Vector3.ZERO
	var toolOffset: Vector3 = Vector3.ZERO
	var radius: Float = 0f
	var isSlowed: Boolean = false
	var isAlive: Boolean = true
	var headRestingState: Transform = Transform.IDENTITY
	var actor: Id = emptyId

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
	var isActive = true
	var moveDirection: Vector3 = Vector3.ZERO
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
		speed = walkSpeed
		toolOffset = (findNode("toolOrigin") as? Spatial)?.translation ?: Vector3.ZERO
		val collisionShape = findNode("shape") as CollisionShape
		radius = getCollisionShapeRadius(collisionShape)
		equippedSprite = findNode("equipped") as? AnimatedSprite3D
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

		val result1 = directionA + directionB
		val result2 = Vector3(result1.x, 0f, result1.z)
		return if (result2.length() > 1f)
			result2.normalized()
		else
			result2
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

	fun jump(input: PlayerInput) {
		if (input.jump) {
			velocity.y = jumpHeight.toDouble()
			snap = Vector3.ZERO
		}
	}

	fun updateSpeed() {
		if (isSlowed) {
			val targetSpeed = walkSpeed / 2.5f
			speed = GD.lerp(speed, targetSpeed, 0.1f)
			if (speed < targetSpeed + 0.01f) {
				isSlowed = false
			}
		} else if (speed < walkSpeed) {
			speed = GD.lerp(speed, walkSpeed, 0.05f)
		}
	}

	fun walk(input: PlayerInput, direction: Vector3, delta: Float) {
		if (isOnFloor()) {
			snap = -getFloorNormal() - getFloorVelocity() * delta

			if (velocity.y < 0f)
				velocity.y = 0.0

			jump(input)
		} else {
			if (snap != Vector3.ZERO && velocity.y != 0.0) {
				velocity.y = 0.0
			}

			snap = Vector3.ZERO
			velocity.y -= (gravity * delta).toDouble()
		}

		updateSpeed()

		accelerate(direction, delta)

		// moveAndSlideWithSnap seems to be an expensive operation so try to minimize how often it is called
		if (velocity != Vector3.ZERO) {
			moveAndSlideWithSnap(velocity, snap, Vector3.UP, true, 4, floorMaxAngle)
		}
	}

	fun update(input: PlayerInput, character: Character, delta: Float) {
		walk(input, moveDirection, delta)
		moveDirection = Vector3.ZERO

		if (isAlive != character.isAlive) {
			collisionMask = if (character.isAlive)
				CollisionMasks.characterMask.toLong()
			else
				CollisionMasks.corpseMask.toLong()

			collisionLayer = if (character.isAlive)
				CollisionMasks.characterLayers.toLong()
			else
				CollisionMasks.none.toLong()

			isAlive = character.isAlive
		}
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		val world = Global.world
		if (world != null && equippedSprite != null) {
			val deck = world.deck
			val character = deck.characters[actor]
			val activeAccessory = if (character?.isAlive == true)
				character.activeAccessory
			else
				emptyId

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
