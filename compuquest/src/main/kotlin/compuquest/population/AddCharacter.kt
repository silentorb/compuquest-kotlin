package compuquest.population

import compuquest.simulation.characters.newCharacterAccessories
import compuquest.simulation.characters.newCharacter
import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.Factions
import compuquest.simulation.definition.ResourceType
import compuquest.simulation.general.*
import compuquest.simulation.intellect.Spirit
import godot.Node
import godot.Resource
import godot.Spatial
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.NextId
import silentorb.mythic.godoting.*

fun parseFaction(faction: Key?, node: Node): Key {
  val rawFaction = faction ?: getString(node, "faction")
  return if (rawFaction == "")
    Factions.neutral
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
  definitions: Definitions,
  id: Id,
  nextId: NextId,
  spatial: Spatial?,
  faction: Key?,
  node: Node,
  additional: List<Any> = listOf()
): Hands {
  return tempCatch {
    val parent = node.getParent()
    val sprite = parent?.findNode("sprite")
//    val depiction = getString(creature, "depiction")
//    sprite?.set("animation", depiction)
//    val refinedFaction = parseFaction(faction, node)
//    val maxHealth = getIntOrNull(creature, "health") ?: 1
//    val wares = getList<Resource>(creature, "wares")
//      .map { addWare(nextId, id, it) }

    val type = getString(node, "type")
    val definition = definitions.characters[type]
    if (definition == null)
      throw Error("Unknown character type: $type")

    val accessories = newCharacterAccessories(definitions, definition, id, nextId)
    val characterBody = parent as? CharacterBody
    val toolOffset = characterBody?.toolOffset ?: Vector3.ZERO

    listOf(
      Hand(
        id = id,
        components =
        listOfNotNull(
          newCharacter(definition, accessories, toolOffset),
          sprite,
          spatial,
        ) + additional
      )
    ) + accessories
  }
}
