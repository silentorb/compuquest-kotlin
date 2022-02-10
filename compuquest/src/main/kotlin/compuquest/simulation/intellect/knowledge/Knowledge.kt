package compuquest.simulation.intellect.knowledge

import compuquest.simulation.characters.*
import compuquest.simulation.general.World
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.design.filterEnemyTargets
import compuquest.simulation.intellect.spiritUpdateInterval
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table

const val relationshipInterval = 120

data class Knowledge(
	val visibleEnemies: Table<Character> = mapOf(),
	val entityLocations: Table<Vector3> = mapOf(),
	val relationships: Relationships = listOf(),
	val relationshipUpdateCounter: Int = relationshipInterval,
)

fun updateRelationships(world: World, actor: Id): Relationships {
	val character = world.deck.characters[actor]
	return if (character != null)
		(character.relationships + getCharacterGroupRelationships(character))
			.distinct()
	else
		listOf()
}

fun updateKnowledge(world: World, actor: Id, spirit: Spirit): Knowledge {
	val knowledge = spirit.knowledge
	val deck = world.deck
	val character = world.deck.characters[actor]!!
	val visibleEnemies = filterEnemyTargets(world, actor, character, spirit.visibilityRange)
	val entityLocations = knowledge.entityLocations.filter { (id, _) ->
		visibleEnemies.containsKey(id) && isCharacterAlive(deck, id)
	}
	val relationshipUpdateCounter = knowledge.relationshipUpdateCounter + spiritUpdateInterval
	val relationships = if (relationshipUpdateCounter >= relationshipUpdateCounter)
		updateRelationships(world, actor)
	else
		knowledge.relationships

	return spirit.knowledge.copy(
		visibleEnemies = visibleEnemies,
		entityLocations = entityLocations,
		relationshipUpdateCounter = relationshipUpdateCounter % relationshipInterval,
		relationships = relationships,
	)
}
