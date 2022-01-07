package compuquest.simulation.characters

import compuquest.simulation.combat.applyDamage
import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.FactionNames
import compuquest.simulation.definition.Factions
import compuquest.simulation.general.*
import godot.core.Vector3
import silentorb.mythic.ent.*
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues
import silentorb.mythic.happening.handleEvents

data class CharacterDefinition(
	val name: String,
	val attributes: Set<Key> = setOf(),
	val depiction: String,
	val frame: Int = 0,
	val faction: Key = FactionNames.neutral,
	val health: Int,
	val accessories: List<Key> = listOf(),
)

data class Character(
	val definition: CharacterDefinition,
	val name: String,
	val faction: Key,
	val health: Int,
	val attributes: Set<Key> = setOf(),
	val fee: Int = 0,
	val enemyVisibilityRange: Float = 0f,
	override val depiction: String,
	override val frame: Int = 0,
	val originalDepiction: String = depiction,
	val activeAccessory: Id? = null,
	val toolOffset: Vector3 = Vector3.ZERO,
) : SpriteState {
	val isAlive: Boolean = health > 0
}

fun getAccessoriesSequence(accessories: Table<Accessory>, actor: Id) =
	accessories
		.asSequence()
		.filter { it.value.owner == actor }

fun hasAccessoryWithEffect(accessories: Table<Accessory>, actor: Id, effect: Key): Boolean =
	getOwnerAccessories(accessories, actor).any { it.value.definition.actionEffects.any { a -> a.type == effect } }

fun getReadyAccessories(world: World, actor: Id): Table<Accessory> =
	getOwnerAccessories(world.deck.accessories, actor)
		.filter { canUse(world, it.value) }

fun canUse(world: World, accessory: Accessory): Boolean {
//	val deck = world.deck
//	val actor = deck.characters[accessory.owner]
//	val faction = deck.factions[actor?.faction]
//	val cost = accessory.definition.cost
	return accessory.cooldown == 0f
}

fun canUse(world: World, accessory: Id): Boolean =
	canUse(world, world.deck.accessories[accessory]!!)

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

fun newCharacterAccessories(
	definitions: Definitions,
	definition: CharacterDefinition,
	id: Id,
	nextId: NextId
): List<Hand> =
	definition.accessories
		.map { accessory ->
			newAccessory(definitions, nextId, id, accessory)
		}

fun newCharacter(definition: CharacterDefinition, accessories: Hands, toolOffset: Vector3) =
	Character(
		definition = definition,
//            name = node.name,
		name = definition.name,
		faction = definition.faction,
		health = definition.health,
		depiction = definition.depiction,
		frame = definition.frame,
//            fee = if (getBoolean(node, "includeFees")) getInt(creature, "fee") else 0,
//            key = getNonEmptyString(creature, "key"),
//    attributes = definition.attributes,
		activeAccessory = accessories.mapNotNull { it.id }.firstOrNull(),
		toolOffset = toolOffset,
//            enemyVisibilityRange = getFloat(creature, "enemyVisibilityRange"),
	)

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
//    body = updateCharacterBody(characterEvents, character.body),
		faction = updateCharacterFaction(characterEvents, character.faction),
	)
}
