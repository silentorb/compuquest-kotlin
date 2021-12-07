package compuquest.population

import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.Hand
import compuquest.simulation.general.World
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.updating.newEntitiesFromHands
import godot.Node
import godot.Spatial
import scripts.entities.actor.AttachPlayer
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.NextId
import silentorb.mythic.godoting.*

const val componentGroup = "component"

fun processComponentNode(
	definitions: Definitions,
	id: Id,
	nextId: NextId,
	spatial: Spatial?,
	faction: Key?,
	node: Node,
	additional: List<Any> = listOf(),
): List<Hand> =
	when {
		getScriptName(node) == "AttachCharacter" -> {
			addCharacter(definitions, id, nextId, spatial, faction, node, additional)
		}
		else -> listOf()
	}

fun newCharacterBody(
	definitions: Definitions, nextId: NextId, spatial: Spatial, components: List<Node>
): List<Hand> {
	return components.flatMap { processComponentNode(definitions, nextId(), nextId, spatial, null, it, listOf(Spirit())) }
}

fun processSceneEntities(root: Node, world: World): World {
	val definitions = world.definitions
	val componentNodes = root.getTree()?.getNodesInGroup(componentGroup)?.filterIsInstance<Node>() ?: listOf()
	val parents = componentNodes
		.mapNotNull { it.getParent() }
		.distinct()

	val spatialNodes = parents.filterIsInstance<Spatial>()
	val nextId = world.nextId.source()

	val hands = spatialNodes
		.flatMap { spatial ->
			val components = componentNodes.filter { it.getParent() == spatial }
			if (components.any { it is AttachPlayer })
				addPlayer(definitions, nextId, spatial, components)
			else {
				newCharacterBody(definitions, nextId, spatial, components)
			}
		}

	val nextWorld = newEntitiesFromHands(hands, world)
	return populateQuests(nextWorld)
}
