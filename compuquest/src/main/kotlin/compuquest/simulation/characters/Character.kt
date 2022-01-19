package compuquest.simulation.characters

import compuquest.simulation.combat.applyDamage
import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.FactionNames
import compuquest.simulation.definition.Factions
import compuquest.simulation.general.*
import compuquest.simulation.intellect.newSpirit
import godot.AnimatedSprite3D
import godot.Node
import godot.PackedScene
import godot.Spatial
import godot.core.Vector3
import scripts.Global
import scripts.entities.CharacterBody
import silentorb.mythic.ent.*
import silentorb.mythic.godoting.getString
import silentorb.mythic.godoting.tempCatch
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues
import silentorb.mythic.happening.handleEvents
import silentorb.mythic.randomly.Dice
import silentorb.mythic.timing.IntTimer
import silentorb.mythic.timing.newTimer

data class CharacterDefinition(
	val name: String,
	val attributes: Set<Key> = setOf(),
	val depiction: String,
	val frame: Int = 0,
	val faction: Key = FactionNames.neutral,
	val health: Int,
	val corpseDecay: Float = 20f,
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
	val isAlive: Boolean = isCharacterAlive(health)
	val corpseDecay: Float get() = definition.corpseDecay
}

fun isCharacterAlive(health: Int): Boolean =
	health > 0

fun isCharacterAlive(deck: Deck, actor: Id): Boolean =
	deck.characters[actor]?.isAlive == true

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
	return accessory.definition.actionEffects.any() && accessory.cooldown == 0f
}

fun canUse(world: World, accessory: Id): Boolean =
	canUse(world, world.deck.accessories[accessory]!!)

const val modifyHealthCommand = "modifyHealth"
const val setHealthCommand = "setHealth"

fun modifyHealth(target: Id, amount: Int) =
	Event(modifyHealthCommand, target, amount)

fun eventsFromCharacter(previous: World): (Id, Character) -> Events = { actor, character ->
	val a = previous.deck.characters[actor]
	if (a?.isAlive == true && !character.isAlive && character.corpseDecay > 0f)
		listOf(newHandEvent(Hand(id = actor, components = listOf(newTimer(character.corpseDecay)))))
	else
		listOf()
}

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

fun newCharacter(definition: CharacterDefinition, accessories: Hands, toolOffset: Vector3, faction: Key? = null) =
	Character(
		definition = definition,
		name = definition.name,
		faction = faction ?: definition.faction,
		health = definition.health,
		depiction = definition.depiction,
		frame = definition.frame,
//            fee = if (getBoolean(node, "includeFees")) getInt(creature, "fee") else 0,
//    attributes = definition.attributes,
		activeAccessory = accessories.mapNotNull { it.id }.firstOrNull(),
		toolOffset = toolOffset,
	)

fun updateCharacterHealth(deck: Deck, actor: Id, characterEvents: Events, character: Character): Int {
	val healthMod = filterEventValues<Int>(modifyHealthCommand, characterEvents)
		.sum() +
			applyDamage(deck, actor, characterEvents)

	val healthSet = filterEventValues<Int>(setHealthCommand, characterEvents)
		.firstOrNull()

	// Directly setting health overrides any other modifiers and is only intended for special cases where the
	// character is effectively damage immune for a frame
	return healthSet ?: modifyResource(healthMod, character.definition.health, character.health)
}

fun updateCharacter(world: World, events: Events): (Id, Character) -> Character = { actor, character ->
	val deck = world.deck
	val characterEvents = events.filter { it.target == actor }
	val health = updateCharacterHealth(deck, actor, characterEvents, character)

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

fun addCharacter(
	definitions: Definitions,
	definition: CharacterDefinition,
	id: Id,
	nextId: NextId,
	characterBody: CharacterBody,
	faction: Key? = null,
	additional: List<Any> = listOf()
): Hands {
	return tempCatch {
		val sprite = characterBody.findNode("sprite") as AnimatedSprite3D?
		val accessories = newCharacterAccessories(definitions, definition, id, nextId)
		val toolOffset = characterBody.toolOffset
		val character = newCharacter(definition, accessories, toolOffset, faction)
		if (sprite != null) {
			sprite.animation = character.depiction
		}

		listOf(
			Hand(
				id = id,
				components = listOfNotNull(
					character,
					sprite,
					characterBody,
				) + additional
			)
		) + accessories
	}
}

fun getRandomizedSpawnOffset(dice: Dice) =
	Vector3(
		dice.getFloat(-0.1f, 0.1f),
		dice.getFloat(0f, 0.1f),
		dice.getFloat(-0.1f, 0.1f)
	)

fun spawnCharacter(
	world: World,
	scene: PackedScene,
	origin: Vector3,
	rotation: Vector3,
	definition: CharacterDefinition,
	faction: Key
) {
	val body = scene.instance() as CharacterBody
	val dice = world.dice
	val definitions = world.definitions
	body.translation = origin + getRandomizedSpawnOffset(dice)

	body.rotation = rotation

	// The body needs to be added to the world before addCharacter because
	// Godot does not call _ready until the node is added to the scene tree
	world.scene.addChild(body)

	val nextId = world.nextId.source()
	val hands = addCharacter(definitions, definition, nextId(), nextId, body, faction, listOf(newSpirit()))
	Global.addHands(hands)
}
