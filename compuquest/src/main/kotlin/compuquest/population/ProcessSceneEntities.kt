package compuquest.population

import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.Hand
import compuquest.simulation.general.World
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.intellect.newSpirit
import compuquest.simulation.updating.newEntitiesFromHands
import godot.Navigation
import godot.Node
import godot.Spatial
import scripts.entities.PlayerSpawner
import scripts.entities.actor.AttachPlayer
import silentorb.mythic.debugging.getDebugBoolean
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
	return components.flatMap {
		processComponentNode(
			definitions,
			nextId(),
			nextId,
			spatial,
			null,
			it,
			listOf(newSpirit())
		)
	}
}

fun processSceneEntities(root: Node, world: World): World {
	val definitions = world.definitions
	val componentNodes = root.getTree()?.getNodesInGroup(componentGroup)?.filterIsInstance<Node>() ?: listOf()
	val parents = componentNodes
		.mapNotNull { it.getParent() }
		.distinct()

	val spatialNodes = parents.filterIsInstance<Spatial>()
	val nextId = world.nextId.source()

	val excludeNonPlayers = getDebugBoolean("NO_MONSTERS")
	val hands = spatialNodes
		.flatMap { spatial ->
			val components = componentNodes.filter { it.getParent() == spatial }
			when {
				components.any { it is AttachPlayer } -> addPlayer(definitions, nextId, spatial, components)
				!excludeNonPlayers -> {
					newCharacterBody(definitions, nextId, spatial, components)
				}
				else -> listOf()
			}
		}

	val playerSpawners = if (getDebugBoolean("PLAYER_RESPAWN"))
		findChildrenOfType<PlayerSpawner>(root)
	else
		listOf()

	val nextWorld = newEntitiesFromHands(hands, world)
		.copy(
			navigation = root.findNode("Navigation") as? Navigation,
			playerSpawners = playerSpawners,
		)

	return populateQuests(nextWorld)
}
