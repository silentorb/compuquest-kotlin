package compuquest.simulation.general

import compuquest.simulation.definition.Definitions
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.updating.newEntitiesFromHands
import godot.Node
import godot.Spatial
import scripts.entities.actor.AttachAccessory
import scripts.entities.actor.AttachCharacter
import scripts.entities.actor.AttachPlayer
import scripts.entities.actor.AttachResource
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.NextId

const val componentGroup = "component"

fun processComponentNode(body: Id, faction: Id, node: Node): List<Any> =
  when (node) {
    is AttachCharacter -> listOf(
      Character(
        name = node.characterName,
        faction = faction,
        depiction = node.depiction,
        health = IntResource(node.healthValue, node.healthMax),
        body = body
      ),
      Spirit(),
    )
    else -> listOf()
  }

fun processSubComponentNode(definitions: Definitions, owner: Id, node: Node): List<Any> =
  when (node) {
    is AttachAccessory -> {
      val definition = definitions.accessories[node.definition]
      if (definition != null)
        listOf(accessoryFromDefinition(definition, owner))
      else
        listOf()
    }
    else -> listOf()
  }

fun processSubComponents(definitions: Definitions, nextId: NextId, owner: Id, components: List<Node>) =
  components.mapNotNull {
    val children = processSubComponentNode(definitions, owner, it)
    if (children.any())
      Hand(
        id = nextId(),
        components = children,
      )
    else
      null
  }

fun newPlayer(
  definitions: Definitions, nextId: NextId, id: Id, spatial: Spatial,
  components: List<Node>, subComponents: List<Node>
): List<Hand> {
  val members = components
    .flatMap { child ->
      val childId = nextId()
      listOf(
        Hand(
          id = childId,
          components = processComponentNode(id, id, child),
        )
      ) + processSubComponents(
        definitions,
        nextId,
        childId,
        subComponents.filter { it.getParent() == child })
    }

  return listOf(
    Hand(
      id = id,
      components = listOf(
        spatial,
        Player(
          faction = id,
          party = members
            .filter { hand -> hand.components.any { it is Character } }
            .mapNotNull { it.id }
        ),
        Faction(
          name = "Player",
          resources = components
            .filterIsInstance<AttachResource>()
            .associate { it.resource to it.amount }
        )
      )
    )
  ) + members
}

fun newCharacterBody(
  definitions: Definitions, nextId: NextId, id: Id, spatial: Spatial,
  components: List<Node>, subComponents: List<Node>
) =
  listOf(
    Hand(
      id = id,
      components = listOf<Any>(
        spatial,
      ) + components.flatMap { processComponentNode(id, 0L, it) }
    )
  ) + components.flatMap { child ->
    processSubComponents(definitions, nextId, id, subComponents.filter { it.getParent() == child })
  }

fun processSceneEntities(root: Node, world: World): World {
  val definitions = world.definitions
  val componentNodes = root.getTree()?.getNodesInGroup(componentGroup)?.filterIsInstance<Node>() ?: listOf()
  val parents = componentNodes
    .mapNotNull { it.getParent() }
    .distinct()

  val spatialNodes = parents.filterIsInstance<Spatial>()
//  val subComponents = componentNodes.filter { !parents.contains(it.getParent()) }
  val nextId = world.nextId.source()

  val hands = spatialNodes
    .flatMap { spatial ->
      val id = nextId()
      val components = componentNodes.filter { it.getParent() == spatial }
      if (components.any { it is AttachPlayer })
        newPlayer(definitions, nextId, id, spatial, components, componentNodes)
      else {
        newCharacterBody(definitions, nextId, id, spatial, components, componentNodes)
      }
    }

  return newEntitiesFromHands(hands, world)
}
