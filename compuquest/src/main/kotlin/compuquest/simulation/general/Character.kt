package compuquest.simulation.general

import compuquest.simulation.combat.applyDamage
import compuquest.simulation.definition.Factions
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.Table
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues
import silentorb.mythic.happening.handleEvents

data class CharacterDefinition(
  val name: String,
  val attributes: Set<Key> = setOf(),
  val depiction: String,
  val frame: Int = 0,
  val faction: Key,
  val health: Int,
)

data class Character(
  val definition: CharacterDefinition,
  val name: String,
  val faction: Key,
  val health: Int,
  val body: Id? = null,
  val attributes: Set<Key> = setOf(),
  val fee: Int = 0,
  val enemyVisibilityRange: Float = 0f,
  override val depiction: String,
  override val frame: Int = 0,
  val originalDepiction: String = depiction,
) : SpriteState {
  val isAlive: Boolean = health > 0
}

fun getAccessoriesSequence(accessories: Table<Accessory>, actor: Id) =
  accessories
    .asSequence()
    .filter { it.value.owner == actor }

fun getAccessories(accessories: Table<Accessory>, actor: Id) =
  accessories
    .filterValues { it.owner == actor }

fun hasAccessoryWithEffect(accessories: Table<Accessory>, actor: Id, effect: Key): Boolean =
  getAccessories(accessories, actor).any { it.value.definition.effect == effect }

fun getReadyAccessories(world: World, actor: Id) =
  getAccessories(world.deck.accessories, actor)
    .filter { canUse(world, it.value) }

fun canUse(world: World, accessory: Accessory): Boolean {
  val deck = world.deck
  val actor = deck.characters[accessory.owner]
  val faction = deck.factions[actor?.faction]
  val cost = accessory.definition.cost
  return accessory.cooldown == 0f && (faction == null ||
      cost.all { faction.resources[it.key] ?: 0 >= it.value })

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
    joinedPlayer -> Factions.player.name
    removeFactionMemberEvent -> Factions.neutral.name
    else -> value
  }
}

fun updateCharacter(world: World, events: Events): (Id, Character) -> Character = { actor, character ->
  val characterEvents = events.filter { it.target == actor }
  val healthMod = filterEventValues<Int>(modifyHealthCommand, characterEvents)
    .sum() +
      applyDamage(world.deck, actor, characterEvents)

  val health = modifyResource(healthMod, character.definition.health, character.health)
  val depiction = if (health == 0)
    "sprites"
  else
    character.originalDepiction

  character.copy(
    health = health,
    depiction = depiction,
    body = updateCharacterBody(characterEvents, character.body),
    faction = updateCharacterFaction(characterEvents, character.faction),
  )
}
