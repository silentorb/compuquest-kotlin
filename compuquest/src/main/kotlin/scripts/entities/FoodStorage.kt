package scripts.entities

import compuquest.definition.Accessories
import compuquest.simulation.combat.DamageTarget
import compuquest.simulation.general.*
import godot.AnimatedSprite3D
import godot.CollisionShape
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.getChildOfType

@RegisterClass
class FoodStorage : Spatial(), DamageTarget, Interactive, EntityNode {

	enum class Mode {
		normal,
		hasFood,
		fire,
		stump,
	}

	@Export
	@RegisterProperty
	var frameOffset: Int = 9 // Defaults to bush for now so at least something works until Godot-Kotlin @Tool works


	@Export
	@RegisterProperty
	// Expressed in frames.
	// -1 means this entity never restocks (effectively is one-time-use)
	// 0 means this entity is continually restocked
	var restockDuration: Int = 60 * 60 // Defaults to 1 minute

	var sprite: AnimatedSprite3D? = null
	var collisionShape: CollisionShape? = null
	var accumulator: Int = 0

	var mode: Mode = Mode.hasFood
		set(value) {
			field = value
			updateModeChange()
		}

	fun updateModeChange() {
		updateSpriteImage()
		updateCollisionShape()
	}

	fun getModeImageFrame(): Int =
		mode.ordinal + frameOffset

	fun updateSpriteImage() {
		sprite?.frame = getModeImageFrame().toLong()
	}

	fun updateCollisionShape() {
		collisionShape?.disabled = mode == Mode.stump
	}

	@RegisterFunction
	override fun _ready() {
		sprite = getChildOfType(this)
		collisionShape = getChildOfType(this)
		updateModeChange()
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		if (mode == Mode.fire) {
			++accumulator
			if (accumulator > 60 * 1) {
				accumulator = 0
				mode = Mode.stump
			}
		} else if (mode == Mode.normal) {
			if (restockDuration > 0) {
				++accumulator
				if (accumulator > restockDuration) {
					accumulator = 0
					mode = Mode.hasFood
				}
			}
		}
	}

	override fun onDamage(world: World, amount: Int, source: Id) {
		if (mode == Mode.normal || mode == Mode.hasFood) {
			mode = Mode.fire
		}
	}

	override fun getInteractable(world: World): Interactable? {
		return if (mode == Mode.hasFood)
			newNodeInteractable(this, InteractionBehaviors.take)
		else
			null
	}

	override fun onInteraction(world: World, actor: Id) {
		if (mode == Mode.hasFood) {
			Global.addHand(newAccessory(world, actor, Accessories.berries))
			if (restockDuration != 0) {
				mode = Mode.normal
			}
		}
	}
}
