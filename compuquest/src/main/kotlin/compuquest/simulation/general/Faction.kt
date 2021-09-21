package compuquest.simulation.general

import compuquest.simulation.definition.ResourceType
import silentorb.mythic.happening.Events
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.KeyTable
import kotlin.math.max

data class Faction(
  val name: String,
  val resources: Map<ResourceType, Int> = mapOf(),
)

enum class RelationshipCategory {
  war,
  neutral,
  ally
}

fun getRelationshipValue(relationships: RelationshipTable, first: Key?, second: Key?): Int =
  relationships[setOf(first, second)] ?: 50

val relationshipDivisions = listOf(
  35, 65
)

typealias RelationshipTable = Map<Set<Key>, Int>

fun getRelationshipCategory(value: Int): RelationshipCategory {
  val index = relationshipDivisions.indexOfFirst { it <= value } + 1
  return RelationshipCategory.values()[index]
}

fun getRelationshipCategory(relationships: RelationshipTable, first: Key?, second: Key?): RelationshipCategory =
  getRelationshipCategory(getRelationshipValue(relationships, first, second))

fun isEnemy(relationships: RelationshipTable, first: Key?, second: Key?): Boolean =
  if (first == null || second == null)
    false
  else
    getRelationshipCategory(relationships, first, second) == RelationshipCategory.war

fun isAlly(relationships: RelationshipTable, first: Key?, second: Key?): Boolean =
  !isEnemy(relationships, first, second)

data class NewFaction(
  val key: String,
  val data: Faction,
)

fun extractFactions(idHands: List<Hand>): KeyTable<Faction> =
  idHands
    .flatMap { hand ->
      hand.components.filterIsInstance<NewFaction>()
        .map { it.key to it.data }
    }
    .associate { it }

fun updateFaction(world: World, events: Events): (Key, Faction) -> Faction {
  val definitions = world.definitions
  val deck = world.deck
  val uses = events
    .filter { it.type == useActionCommand }
    .mapNotNull { event ->
      val accessory = deck.accessories[event.target]
      val character = world.deck.characters[accessory?.owner]
      if (accessory?.cost != null && character != null) {
        Pair(character.faction, accessory.cost)
      } else
        null
    }

  return { id, faction ->
    val factionUses = uses.filter { f -> f.first == id }
    faction.copy(
      resources = faction.resources.mapValues { (resourceType, amount) ->
        val expense = factionUses
          .filter { (_, cost) -> cost.resource == resourceType }
          .sumBy { (_, cost) -> cost.amount }

        max(0, amount - expense)
      }
    )
  }
}
