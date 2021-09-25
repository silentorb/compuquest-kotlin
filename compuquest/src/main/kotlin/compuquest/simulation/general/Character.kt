package compuquest.simulation.general

import compuquest.simulation.definition.Factions
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.Table
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues
import silentorb.mythic.happening.handleEvents

data class Character(
  val name: String,
  val faction: Key,
  val health: IntResource,
  val body: Id? = null,
  val fee: Int = 0,
  override val depiction: String,
  override val frame: Int = 0,
) : SpriteState {
  val isAlive: Boolean = health.value > 0
}

fun getAccessories(accessories: Table<Accessory>, actor: Id) =
  accessories.filterValues { it.owner == actor }

fun getReadyAccessories(world: World, actor: Id) =
  getAccessories(world.deck.accessories, actor)
    .filter { canUse(world, it.value) }

fun canUse(world: World, accessory: Accessory): Boolean {
  val deck = world.deck
  val actor = deck.characters[accessory.owner]
  val faction = deck.factions[actor?.faction]
  val cost = accessory.cost
  return accessory.cooldown == 0f && (faction == null ||
      (cost == null || faction.resources[cost.resource] ?: 0 >= cost.amount)
      )
}

const val modifyHealthCommand = "modifyHealth"

fun modifyHealth(target: Id, amount: Int) =
  Event(modifyHealthCommand, target, amount)

//fun eventsFromCharacter(world: World, previous: World?): (Id, Character) -> Events = { actor, character ->
//  if (previous != null) {
//    val a = previous.deck.characters[actor]
//    if (a?.isAlive ?: true && !character.isAlive)
//      listOf(setDepiction)
//    else
//      listOf()
//  } else
//    listOf()
//}

val updateCharacterBody = handleEvents<Id?> { event, value ->
  when (event.type) {
    joinedPlayer -> event.value as Id
    else -> value
  }
}

val updateCharacterFaction = handleEvents<Key> { event, value ->
  when (event.type) {
    joinedPlayer -> Factions.player
    removeFactionMemberEvent -> Factions.neutral
    else -> value
  }
}

fun updateCharacter(world: World, events: Events): (Id, Character) -> Character = { actor, character ->
  val characterEvents = events.filter { it.target == actor }
  val healthMod = filterEventValues<Int>(modifyHealthCommand, characterEvents)
    .sum()

  val health = modifyResource(healthMod, character.health)
  val depiction = if (health.value == 0)
    "sprites"
  else
    character.depiction

  character.copy(
    health = health,
    depiction = depiction,
    body = updateCharacterBody(characterEvents, character.body),
    faction = updateCharacterFaction(characterEvents, character.faction)
  )
}
