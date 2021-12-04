package compuquest.population

import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.Hand
import compuquest.simulation.general.World
import compuquest.simulation.updating.newEntitiesFromHands
import godot.Node
import godot.Resource
import godot.Spatial
import scripts.entities.actor.AttachPlayer
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.NextId
import silentorb.mythic.godoting.*

const val componentGroup = "component"

fun processComponentNode(definitions: Definitions, nextId: NextId, spatial: Spatial?, body: Id?, faction: Key?, node: Node): List<Hand> =
  when {
    getScriptName(node) == "AttachCharacter" -> {
      val creature = node.get("creature") as? Resource
      if (creature == null)
        listOf()
      else
        addCharacter(definitions, nextId, spatial, body, creature, faction, node)
    }
    else -> listOf()
  }

fun newCharacterBody(
  definitions: Definitions, nextId: NextId, spatial: Spatial, components: List<Node>
): List<Hand> {
  return components.flatMap { processComponentNode(definitions, nextId, spatial, null, null, it) }
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
