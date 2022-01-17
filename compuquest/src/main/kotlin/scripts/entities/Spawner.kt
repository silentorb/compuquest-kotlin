package scripts.entities

import compuquest.simulation.characters.addCharacter
import compuquest.simulation.intellect.Spirit
import godot.PackedScene
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector3
import godot.global.GD
import scripts.Global

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
	var frequency: Float = 0f

	@Export
	@RegisterProperty
	var quantity: Int = 1

	var accumulator: Float = 0f
	var firstSpawn = false

	fun spawn() {
		val world = Global.world
		if (world != null) {
			firstSpawn = true
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
					val nextId = world.nextId.source()
					val hands = addCharacter(definitions, definition, nextId(), nextId, body, faction, listOf(Spirit()))
					Global.addHands(hands)
				}
			}
		}
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		accumulator += delta.toFloat()
		if (accumulator >= frequency || !firstSpawn) {
			accumulator -= frequency
			spawn()
		}
	}
}
