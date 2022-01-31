package compuquest.simulation.intellect.knowledge

import compuquest.simulation.characters.Character
import compuquest.simulation.characters.isCharacterAlive
import compuquest.simulation.general.World
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.design.filterEnemyTargets
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table

data class Knowledge(
	val visibleEnemies: Table<Character> = mapOf(),
	val entityLocations: Table<Vector3> = mapOf(),
)

fun updateKnowledge(world: World, actor: Id, spirit: Spirit): Knowledge {
	val knowledge = spirit.knowledge
	val deck = world.deck
	val character = world.deck.characters[actor]!!
	val visibleEnemies = filterEnemyTargets(world, actor, character, spirit.visibilityRange)
	val entityLocations = knowledge.entityLocations.filter { (id, _) ->
		visibleEnemies.containsKey(id) && isCharacterAlive(deck, id)
	}

	return spirit.knowledge.copy(
		visibleEnemies = visibleEnemies,
		entityLocations = entityLocations,
	)
}
