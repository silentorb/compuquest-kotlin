package scripts.entities

import compuquest.population.getDirectRelationshipAttachments
import compuquest.simulation.characters.Relationships
import compuquest.simulation.characters.getRandomizedSpawnOffset
import compuquest.simulation.characters.spawnAiCharacter
import compuquest.simulation.intellect.design.Goal
import compuquest.simulation.intellect.knowledge.Personality
import compuquest.simulation.intellect.newSpirit
import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import scripts.Global
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.debugging.getDebugInt

@RegisterClass
class Spawner : Spatial() {

	@Export
	@RegisterProperty
	var type: String = ""

	// An interval of 0 is interpreted as spawning only triggered once on the first frame of the spawner
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
	var relationshipCache: Relationships? = null

	fun spawn(): Boolean {
		if (getDebugBoolean("NO_MONSTERS"))
			return true

		val world = Global.world
		return if (world != null) {
			val relationships = relationshipCache ?: getDirectRelationshipAttachments(world.deck, this)
			relationshipCache = relationships
			val goals = getChildren().filterIsInstance<GoalAttachment>()
			val pathDestinations = goals.mapNotNull { it.destination }
			val definition = world.definitions.characters[type]!!
			val multiplier = getDebugInt("MONSTER_SPAWN_MULTIPLIER") ?: 1

			for (i in (0 until quantity * multiplier)) {
				val spirit = newSpirit().copy(
					goal = Goal(
						pathDestinations = pathDestinations,
					),
					personality = definition.personality ?: Personality()
				)

				val hands = spawnAiCharacter(
					world,
					globalTransform.translated(getRandomizedSpawnOffset(world.dice)),
					type,
					relationships = relationships,
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
			if (!firstSpawn && spawn()) {
				firstSpawn = true
			}
			if (interval > 0f) {
				accumulator += delta.toFloat()
				if (accumulator >= interval && spawn()) {
					accumulator -= interval
				}
			}
		}
	}
}
