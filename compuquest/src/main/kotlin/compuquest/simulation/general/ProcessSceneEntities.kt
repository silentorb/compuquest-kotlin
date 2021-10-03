package compuquest.simulation.general

import compuquest.population.addCharacter
import compuquest.population.addPlayer
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

fun processComponentNode(nextId: NextId, spatial: Spatial?, body: Id?, faction: Key?, node: Node): List<Hand> =
  when {
    getScriptName(node) == "AttachCharacter" -> {
      val creature = node.get("creature") as? Resource
      if (creature == null)
        listOf()
      else
        addCharacter(nextId, spatial, body, creature, faction, node)
    }
    else -> listOf()
  }


fun newCharacterBody(
  nextId: NextId, spatial: Spatial, components: List<Node>
): List<Hand> {
  return components.flatMap { processComponentNode(nextId, spatial, null, null, it) }
}

fun processSceneEntities(root: Node, world: World): World {
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
        addPlayer(nextId, spatial, components)
      else {
        newCharacterBody(nextId, spatial, components)
      }
    }

  return newEntitiesFromHands(hands, world)
}
