package scripts.entities

import compuquest.definition.Accessories
import compuquest.simulation.combat.DamageTarget
import compuquest.simulation.general.*
import godot.AnimatedSprite3D
import godot.CollisionShape
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.getChildOfType

@RegisterClass
class Bush : Spatial(), DamageTarget, Interactive {

	enum class BushMode {
		normal,
		berries,
		fire,
		stump,
	}

	var sprite: AnimatedSprite3D? = null
	var collisionShape: CollisionShape? = null
	var accumulator: Int = 0

	var mode: BushMode = BushMode.berries
		set(value) {
			field = value
			updateModeChange()
		}

	fun updateModeChange() {
		updateSpriteImage()
		updateCollisionShape()
	}

	fun getModeImageFrame(): Int =
		mode.ordinal + 9

	fun updateSpriteImage() {
		sprite?.frame = getModeImageFrame().toLong()
	}

	fun updateCollisionShape() {
		collisionShape?.disabled = mode == BushMode.stump
	}

	@RegisterFunction
	override fun _ready() {
		sprite = getChildOfType(this)
		collisionShape = getChildOfType(this)
		updateModeChange()
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		if (mode == BushMode.fire) {
			++accumulator
			if (accumulator > 60 * 1) {
				accumulator = 0
				mode = BushMode.stump
			}
		} else if (mode == BushMode.normal) {
			++accumulator
			if (accumulator > 60 * 1) {
				accumulator = 0
				mode = BushMode.berries
			}
		}
	}

	override fun onDamage(world: World, amount: Int, source: Id) {
		if (mode == BushMode.normal || mode == BushMode.berries) {
			mode = BushMode.fire
		}
	}

	override fun getInteractable(world: World): Interactable? {
		return if (mode == BushMode.berries)
			newNodeInteractable(this, InteractionBehaviors.take)
		else
			null
	}

	override fun onInteraction(world: World, actor: Id) {
		if (mode == BushMode.berries) {
			Global.addHand(newAccessory(world, actor, Accessories.berries))
			mode = BushMode.normal
		}
	}
}
