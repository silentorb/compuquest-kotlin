package scripts.entities

import compuquest.simulation.characters.addCharacter
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.newSpirit
import godot.PackedScene
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector3
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
			val definitions = world.definitions
			val definition = definitions.characters[type]
			if (definition != null) {
				val scene = GD.load<PackedScene>("res://entities/actor/ActorBodyCapsule.tscn")!!
				for (i in (0 until quantity)) {
					val body = scene.instance() as CharacterBody
					val dice = world.dice
					body.translation = globalTransform.origin + Vector3(
						dice.getFloat(-0.1f, 0.1f),
						dice.getFloat(0f, 0.1f),
						dice.getFloat(-0.1f, 0.1f)
					)

					// The body needs to be added to the world before addCharacter because
					// Godot does not call _ready until the node is added to the scene tree
					world.scene.addChild(body)

					val nextId = world.nextId.source()
					val hands = addCharacter(definitions, definition, nextId(), nextId, body, faction, listOf(newSpirit()))
					Global.addHands(hands)
				}
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
