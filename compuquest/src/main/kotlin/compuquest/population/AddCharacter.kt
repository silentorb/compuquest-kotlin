package compuquest.population

import compuquest.simulation.definition.Factions
import compuquest.simulation.definition.ResourceType
import compuquest.simulation.definition.TypedResource
import compuquest.simulation.general.*
import compuquest.simulation.intellect.Spirit
import godot.Resource
import godot.Spatial
import scripts.entities.actor.AttachCharacter
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.NextId
import silentorb.mythic.ent.Table
import silentorb.mythic.godoting.*

fun addAccessory(nextId: NextId, owner: Id, accessory: Resource): Hand {
  val costResource = getString(accessory, "costResource")
  return Hand(
    id = nextId(),
    components = listOf(
      Accessory(
        owner = owner,
        name = getString(accessory, "name"),
        maxCooldown = getFloat(accessory, "cooldown"),
        range = getFloat(accessory, "Range"),
        cost = mapOf(
          (ResourceType.values().firstOrNull { it.name == costResource } ?: ResourceType.mana) to
          getInt(accessory, "costAmount")
        ),
        spawns = (accessory.get("spawns") as? Resource)?.resourcePath,
        effect = getString(accessory, "effect"),
        strength = getFloat(accessory, "strength"),
        attributes = getList<Key>(accessory, "attributes").toSet()
      )
    )
  )
}

fun parseFaction(faction: Key?, node: AttachCharacter): Key {
  val rawFaction = faction ?: node.faction
  return if (rawFaction == "")
    Factions.neutral
  else
    rawFaction
}

fun addQuests(nextId: NextId, client: Id, creature: Resource): Hands {

  return getList<Resource>(creature, "quests")
    .map { quest ->
      Hand(
        id = nextId(),
        components = listOf(
          Quest(
            client = client,
            type = getString(quest, "type"),
            reward = mapOf(ResourceType.gold to getInt(quest, "rewardGold")),
            recipient = getNonEmptyString(quest, "recipient"),
          )
        )
      )
    }
}

fun addCharacter(
  nextId: NextId,
  spatial: Spatial?,
  body: Id?,
  creature: Resource,
  faction: Key?,
  node: AttachCharacter
): Hands {
  val id = nextId()
  val sprite = node.getParent()?.findNode("sprite")
  val depiction = getString(creature, "depiction")
  sprite?.set("animation", depiction)
  val refinedFaction = parseFaction(faction, node)

  return listOf(
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
          key = getNonEmptyString(creature, "key"),
          isForHire = node.isForHire,
          isClient = node.isClient,
        ),
        sprite,
        spatial,
        Spirit(),
      )
    )
  ) + getVariantArray<Resource>("accessories", creature)
    .map { accessory ->
      addAccessory(nextId, id, accessory)
    } + addQuests(nextId, id, creature)
}
