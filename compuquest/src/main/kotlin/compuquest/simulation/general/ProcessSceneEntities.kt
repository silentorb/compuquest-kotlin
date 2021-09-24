package compuquest.simulation.general

import compuquest.simulation.definition.TypedResource
import compuquest.simulation.definition.Factions
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
import silentorb.mythic.godoting.*

const val componentGroup = "component"

fun processComponentNode(nextId: NextId, spatial: Spatial?, body: Id?, faction: Key?, node: Node): List<Hand> =
  when (node) {
    is AttachCharacter -> {
      val creature = node.creature
      if (creature == null)
        listOf()
      else {
        val id = nextId()
        val sprite = node.getParent()?.findNode("sprite")
        val depiction = getString(creature, "depiction")
        sprite?.set("animation", depiction)
        val rawFaction = faction ?: node.faction
        val refinedFaction = if (rawFaction == "")
          Factions.neutral
        else
          rawFaction

        listOf(
          Hand(
            id = id,
            components =
            listOfNotNull(
              Character(
                name = node.name,
                faction = refinedFaction,
                health = IntResource(node.healthValue, getIntOrNull(creature, "health") ?: 1),
                body = body ?: id,
                depiction = depiction,
                fee = if (node.includeFees) getInt(creature, "fee") else 0,
              ),
              sprite,
              spatial,
              Spirit(),
            )
          )
        ) + getVariantArray<Resource>("accessories", creature)
          .map { accessory ->
            val costResource = getString(accessory, "costResource")
            Hand(
              id = nextId(),
              components = listOf(
                Accessory(
                  owner = id,
                  name = getString(accessory, "name"),
                  maxCooldown = getFloat(accessory, "cooldown"),
                  range = getFloat(accessory, "Range"),
                  cost = TypedResource(
                    resource = ResourceType.values().firstOrNull { it.name == costResource } ?: ResourceType.mana,
                    amount = getInt(accessory, "costAmount"),
                  ),
                  spawns = (accessory.get("spawns") as? Resource)?.resourcePath,
                  effect = getString(accessory, "effect"),
                  strength = getFloat(accessory, "strength"),
                  attributes = getList<Key>(accessory, "attributes").toSet()
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
      processComponentNode(nextId, null, id, faction, child)
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
              .associate { attachment ->
                (ResourceType.values().firstOrNull { it.name == attachment.resource } ?: ResourceType.none) to attachment.amount
              }
          )
        )
      )
    )
  ) + memberHands
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
        newPlayer(nextId, spatial, components)
      else {
        newCharacterBody(nextId, spatial, components)
      }
    }

  return newEntitiesFromHands(hands, world)
}
