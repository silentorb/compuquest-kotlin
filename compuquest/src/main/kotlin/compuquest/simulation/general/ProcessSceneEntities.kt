package compuquest.simulation.general

import compuquest.simulation.updating.newEntitiesFromHands
import godot.Node
import godot.Spatial
import scripts.entities.actor.AddCharacter
import scripts.entities.actor.PlayerBody
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.NextId

const val componentGroup = "component"

fun processComponentNode(body: Id, faction: Id, node: Node): Any? =
  when (node) {
    is AddCharacter -> Character(
      name = node.characterName,
      faction = faction,
      depiction = node.depiction,
      health = IntResource(node.healthValue, node.healthMax),
      body = body
    )
    else -> null
  }

fun newPlayer(nextId: NextId, id: Id, spatial: Spatial, components: List<Node>) =
  listOf(
    Hand(
      id = id,
      components = listOf(
        spatial,
        Player(id),
        Faction(name = "Player")
      )
    )
  ) + components.map {
    Hand(
      id = nextId(),
      components = listOfNotNull(processComponentNode(id, id, it)),
    )
  }

fun newCharacterBody(nextId: NextId, id: Id, spatial: Spatial, components: List<Node>) =
  listOf(
    Hand(
      id = id,
      components = listOf(
        spatial,
      ) + components.mapNotNull { processComponentNode(id, 0L, it) }
    )
  )

fun processSceneEntities(root: Node, world: World): World {
  val componentNodes = root.getTree()?.getNodesInGroup(componentGroup)?.filterIsInstance<Node>() ?: listOf()
  val spatialNodes = componentNodes
    .mapNotNull { it.getParent() }
    .distinct()
    .filterIsInstance<Spatial>()

  val nextId = world.nextId.source()

  val hands = spatialNodes
    .flatMap { spatial ->
      val id = nextId()
      val components = componentNodes.filter { it.getParent() == spatial }
      if (spatial is PlayerBody)
        newPlayer(nextId, id, spatial, components)
      else {
        newCharacterBody(nextId, id, spatial, components)
      }
    }

  return newEntitiesFromHands(hands, world)
}
