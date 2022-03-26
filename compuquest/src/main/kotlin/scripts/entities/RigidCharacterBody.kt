package scripts.entities

import compuquest.simulation.characters.Character
import compuquest.simulation.characters.getOwnerAccessory
import compuquest.simulation.input.PlayerInput
import compuquest.simulation.physics.CollisionMasks
import godot.*
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
class RigidCharacterBody : RigidBody(), CharacterBody {

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
		get() = head?.rotation ?: Vector3.ZERO
		set(value) {
			head?.rotation = value
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

override 	var equippedSprite: AnimatedSprite3D? = null

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
		}

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
		velocity = moveDirection

//		updateSpeed()
//
//		accelerate(direction, delta)

		// moveAndSlideWithSnap seems to be an expensive operation so try to minimize how often it is called.
		// Despite what many say, lowering maxSlides makes hardly any difference.
		// Even with maxSlides set to 1 moveAndSlide is incredibly slow.
		if (velocity != Vector3.ZERO || linearVelocity != Vector3.ZERO) {
			val offset = velocity * 8 - linearVelocity
			offset.y = 0.0
			if (offset != Vector3.ZERO)
				applyImpulse(location + Vector3(0, 0.45, 0), offset)
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
				character.activeAccessory
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
