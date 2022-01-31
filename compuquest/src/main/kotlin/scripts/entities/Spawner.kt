package scripts.entities

import compuquest.simulation.characters.spawnCharacter
import compuquest.simulation.intellect.design.Goal
import compuquest.simulation.intellect.newSpirit
import godot.PackedScene
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.global.GD
import scripts.Global
import silentorb.mythic.debugging.getDebugBoolean

@RegisterClass
class Spawner : Spatial() {

	@Export
	@RegisterProperty
	var type: String = ""

	@Export
	@RegisterProperty
	var faction: String = ""

	@Export
	@RegisterProperty
	var interval: Float = 0f

	@Export
	@RegisterProperty
	var active: Boolean = true

	@Export
	@RegisterProperty
	var quantity: Int = 1

	var accumulator: Float = 0f
	var firstSpawn = false

	fun spawn(): Boolean {
		if (getDebugBoolean("NO_MONSTERS"))
			return true

		val world = Global.world
		return if (world != null) {
			val scene = GD.load<PackedScene>("res://entities/actor/ActorBodyCapsule.tscn")!!
			val goals = getChildren().filterIsInstance<GoalAttachment>()
			val pathDestinations = goals.mapNotNull { it.destination }

			for (i in (0 until quantity)) {
				val spirit = newSpirit().copy(
					goal = Goal(
						pathDestinations = pathDestinations,
					)
				)

				val hands = spawnCharacter(
					world,
					scene,
					globalTransform.origin,
					rotation,
					type,
					faction,
					additional = listOf(spirit)
				)
				Global.addHands(hands)
			}
			true
		} else
			false
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		if (active) {
			accumulator += delta.toFloat()
			if (!firstSpawn && spawn()) {
				firstSpawn = true
			}
			if (accumulator >= interval && spawn()) {
				accumulator -= interval
			}
		}
	}
}
