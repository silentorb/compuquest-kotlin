package compuquest.population

import compuquest.simulation.definition.Factions
import compuquest.simulation.definition.ResourceType
import compuquest.simulation.general.*
import compuquest.simulation.intellect.Spirit
import godot.Node
import godot.Resource
import godot.Spatial
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.NextId
import silentorb.mythic.godoting.*

fun addAccessory(nextId: NextId, owner: Id, accessory: Resource): Hand {
  return Hand(
    id = nextId(),
    components = listOf(
      Accessory(
        owner = owner,
        name = getString(accessory, "name"),
        maxCooldown = getFloat(accessory, "cooldown"),
        range = getFloat(accessory, "Range"),
        cost = mapOf(
          (getResourceType(accessory, "costResource") ?: ResourceType.mana) to
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

fun parseFaction(faction: Key?, node: Node): Key {
  val rawFaction = faction ?: getString(node, "faction")
  return if (rawFaction == "")
    Factions.neutral.name
  else
    rawFaction
}

fun addQuests(nextId: NextId, client: Id, creature: Resource): Hands =
  getList<Resource>(creature, "quests")
    .map { quest ->
      Hand(
        id = nextId(),
        components = listOf(
          Quest(
            client = client,
            name = getString(quest, "name"),
            type = getString(quest, "type"),
            reward = mapOf(ResourceType.gold to getInt(quest, "rewardGold")),
            recipientName = getNonEmptyString(quest, "recipient"),
            duration = getInt(quest, "duration"),
            penaltyValue = getInt(quest, "penaltyValue"),
          )
        )
      )
    }

fun addCharacter(
  nextId: NextId,
  spatial: Spatial?,
  body: Id?,
  creature: Resource,
  faction: Key?,
  node: Node
): Hands {
  return tempCatch {
    val id = nextId()
    val sprite = node.getParent()?.findNode("sprite")
    val depiction = getString(creature, "depiction")
    sprite?.set("animation", depiction)
    val refinedFaction = parseFaction(faction, node)
    val maxHealth = getIntOrNull(creature, "health") ?: 1
    val wares = getList<Resource>(creature, "wares")
      .map { addWare(nextId, id, it) }

    listOf(
      Hand(
        id = id,
        components =
        listOfNotNull(
          Character(
            name = node.name,
            faction = refinedFaction,
            health = IntResource(maxHealth),
            body = body ?: id,
            depiction = depiction,
            frame = getInt(creature, "frame"),
            fee = if (getBoolean(node, "includeFees")) getInt(creature, "fee") else 0,
            key = getNonEmptyString(creature, "key"),
            attributes = getList<String>(creature, "attributes").toSet(),
            enemyVisibilityRange = getFloat(creature, "enemyVisibilityRange"),
          ),
          sprite,
          spatial,
          Spirit(),
        )
      )
    ) + getVariantArray<Resource>(creature, "accessories")
      .map { accessory ->
        addAccessory(nextId, id, accessory)
      } + addQuests(nextId, id, creature) + wares
  }
}
