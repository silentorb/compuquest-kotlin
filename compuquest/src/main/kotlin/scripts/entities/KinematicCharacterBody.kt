package scripts.entities

import compuquest.clienting.audio.SpatialSound
import compuquest.clienting.audio.playSound
import compuquest.definition.Sounds
import compuquest.simulation.characters.Character
import compuquest.simulation.characters.getOwnerAccessory
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
import kotlin.math.max
import kotlin.math.min

@RegisterClass
class KinematicCharacterBody : KinematicBody(), CharacterBody {

	override var head: Spatial? = null
	var snap: Vector3 = Vector3.ZERO
	override var velocity = Vector3.ZERO
	override var radius: Float = 0f
	override var isSlowed: Boolean = false
	override var isAlive: Boolean = true
	override var headRestingState: Transform = Transform.IDENTITY
	override var actor: Id = emptyId
	override var sprite: AnimatedSprite3D? = null
	override var location: Vector3 = Vector3.ZERO
	override var isFlying: Boolean = false
	override var playerController: PlayerController? = null

	var toolNode: Spatial? = null

	override fun getToolTransform(): Transform =
		toolNode!!.globalTransform

	override var facing: Vector3
		get() = rotation
		set(value) {
			rotation = value
		}

	companion object {
		val floorMaxAngle = GD.deg2rad(46f)
	}

	@Export
	@RegisterProperty
	var gravity: Float = 30f

	override var walkSpeed: Float = 10f

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

	override var speed: Float = 0f
	override var isActive = true
	override var moveDirection: Vector3 = Vector3.ZERO
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

	override var equippedSprite: AnimatedSprite3D? = null

	@RegisterFunction
	override fun _ready() {
		speed = walkSpeed
		toolNode = findNode("toolOrigin") as? Spatial
		val collisionShape = findNode("shape") as CollisionShape
		radius = getCollisionShapeRadius(collisionShape)
		equippedSprite = findNode("equipped") as? AnimatedSprite3D
		sprite = findNode("sprite") as AnimatedSprite3D?
	}

	override fun directionInput(moveAxis: Vector3): Vector3 {
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
			if (velocity.y < 0f) {
				if (velocity.y < -4f) {

					Global.addEvent(playSound(SpatialSound(
						type = Sounds.feetLanding,
						location = globalTransform.origin,
						volume = min(1f, -velocity.y.toFloat() / 6f),
					)))
				}
				velocity.y = 0.0
			}
			if (input.mobilityAction) {
				velocity.y = jumpHeight.toDouble()
				snap = Vector3.ZERO
			} else {
				snap = -getFloorNormal() - getFloorVelocity() * delta
			}
		} else {
			if (isFlying) {
				snap = Vector3.ZERO
				if (input.fly != 0) {
					velocity.y = (jumpHeight * input.fly.toFloat()).toDouble()
				} else {
					velocity.y = 0.0
				}
			} else {
				if (snap != Vector3.ZERO && velocity.y != 0.0) {
					velocity.y = 0.0
				}

				snap = Vector3.ZERO
				velocity.y = max(-gravity, velocity.y.toFloat() - (gravity * delta)).toDouble()
			}
		}

		updateSpeed()

		accelerate(direction, delta)

		// moveAndSlideWithSnap seems to be an expensive operation so try to minimize how often it is called.
		// Despite what many say, lowering maxSlides makes hardly any difference.
		// Even with maxSlides set to 1 moveAndSlide is incredibly slow.
		if (velocity != Vector3.ZERO) {
			if (snap != Vector3.ZERO) {
				val k = moveAndSlideWithSnap(velocity, snap, Vector3.UP, true, 4, floorMaxAngle)
				val j = k
			} else {
				val k = moveAndSlide(velocity, Vector3.UP, true, 4, floorMaxAngle)
				val j = k
			}
		}
	}

	override fun update(input: PlayerInput, character: Character, delta: Float) {
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
				character.primaryAccessory
			else
				emptyId

			if (activeAccessory != emptyId) {
				val accessory = getOwnerAccessory(deck, actor, activeAccessory)
				if (accessory != null) {
					val nextEquippedFrame = accessory.definition.equippedFrame
					val cooldownOffset = if (accessory.cooldown > 0) 1 else 0
					equippedFrame = nextEquippedFrame + cooldownOffset
				}
			} else {
				equippedFrame = -1
			}
		}
	}
}
