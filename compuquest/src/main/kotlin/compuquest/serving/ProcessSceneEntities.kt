package compuquest.simulation.general

import compuquest.godoting.getVariantArray
import compuquest.simulation.definition.Cost
import compuquest.simulation.definition.ResourceType
import compuquest.simulation.intellect.Spirit
import compuquest.simulation.updating.newEntitiesFromHands
import godot.Node
import godot.Resource
import godot.Spatial
import scripts.entities.actor.AttachCharacter
import scripts.entities.actor.AttachPlayer
import scripts.entities.actor.AttachResource
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.NextId

const val componentGroup = "component"

fun processComponentNode(nextId: NextId, body: Id, faction: Key?, node: Node): List<Hand> =
  when (node) {
    is AttachCharacter -> {
      val creature = node.creature
      if (creature == null)
        listOf()
      else {
        val id = nextId()
        val sprite = node.getParent()?.findNode("sprite")
        val depiction = creature.get("depiction") as? String ?: ""
        sprite?.set("animation", depiction)
        listOf(
          Hand(
            id = id,
            components =
            listOfNotNull(
              Character(
                name = node.name,
                faction = faction ?: node.faction,
                health = IntResource(node.healthValue, (creature.get("health") as? Long)?.toInt() ?: 1),
                body = body,
                depiction = depiction,
              ),
              sprite,
              Spirit(),
            )
          )
        ) + getVariantArray<Resource>("accessories", creature)
          .map { accessory ->
            val costResource = accessory.get("costResource") as? String ?: ""
            Hand(
              id = nextId(),
              components = listOf(
                Accessory(
                  owner = id,
                  name = accessory.get("name") as? String ?: "",
                  maxCooldown = (accessory.get("cooldown") as? Double)?.toFloat() ?: 0f,
                  range = (accessory.get("Range") as? Double?)?.toFloat() ?: 0f,
                  cost = Cost(
                    resource = ResourceType.values().firstOrNull { it.name == costResource } ?: ResourceType.mana,
                    amount = (accessory.get("costAmount") as? Long)?.toInt() ?: 0,
                  ),
                  spawns = (accessory.get("spawns") as? Resource)?.resourcePath,
                )
              )
            )
          }
      }
    }
    else -> listOf()
  }

fun newPlayer(
  nextId: NextId, spatial: Spatial, components: List<Node>
): List<Hand> {
  val faction = playerFaction
  val id = nextId()
  val memberHands = components
    .flatMap { child ->
      processComponentNode(nextId, id, faction, child)
    }

  return listOf(
    Hand(
      id = id,
      components = listOf(
        spatial,
        Player(
          faction = faction,
          party = memberHands
            .filter { hand -> hand.components.any { it is Character } }
            .mapNotNull { it.id }
        ),
        NewFaction(faction,
          Faction(
            name = "Player",
            resources = components
              .filterIsInstance<AttachResource>()
              .associate { it.resource to it.amount }
          )
        )
      )
    )
  ) + memberHands
}

fun newCharacterBody(
  nextId: NextId, spatial: Spatial, components: List<Node>
): List<Hand> {
  val id = nextId()
  return listOf(
    Hand(
      id = id,
      components = listOf<Any>(
        spatial,
      )
    )
  ) + components.flatMap { processComponentNode(nextId, id, null, it) }
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
        newPlayer(nextId, spatial, components)
      else {
        newCharacterBody(nextId, spatial, components)
      }
    }

  return newEntitiesFromHands(hands, world)
}
