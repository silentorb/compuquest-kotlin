package compuquest.simulation.characters

import compuquest.simulation.combat.Destructible
import compuquest.simulation.combat.updateDestructible
import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.Factions
import compuquest.simulation.general.*
import compuquest.simulation.input.ActionChange
import compuquest.simulation.input.PlayerInput
import compuquest.simulation.input.PlayerInputs
import compuquest.simulation.input.emptyPlayerInput
import godot.AnimatedSprite3D
import godot.PackedScene
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.ent.*
import silentorb.mythic.godoting.tempCatch
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.handleEvents
import silentorb.mythic.randomly.Dice
import silentorb.mythic.timing.newTimer

data class CharacterDefinition(
	val name: String,
	val attributes: Set<Key> = setOf(),
	val depiction: String,
	val frame: Int = 0,
	val faction: Key = Factions.neutral,
	val health: Int,
	val corpseDecay: Float = 10f,
	val accessories: List<Key> = listOf(),
)

data class Character(
	val definition: CharacterDefinition,
	val name: String,
	val faction: Key,
	val destructible: Destructible,
	val attributes: Set<Key> = setOf(),
	val fee: Int = 0,
	val enemyVisibilityRange: Float = 0f,
	override val depiction: String,
	override val frame: Int = 0,
	val originalDepiction: String = depiction,
	val activeAccessory: Id = emptyId,
	val toolOffset: Vector3 = Vector3.ZERO,
) : SpriteState {
	val isAlive: Boolean = isCharacterAlive(health)
	val corpseDecay: Float get() = definition.corpseDecay
	val health: Int get() = destructible.health
}

fun isCharacterAlive(health: Int): Boolean =
	health > 0

fun isCharacterAlive(deck: Deck, actor: Id): Boolean =
	deck.characters[actor]?.isAlive == true

fun getAccessoriesSequence(accessories: Table<Accessory>, actor: Id) =
	accessories
		.asSequence()
		.filter { it.value.owner == actor }

fun getActionsSequence(accessories: Table<Accessory>, actor: Id) =
	getAccessoriesSequence(accessories, actor)
		.filter { it.value.canBeActivated }

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
		joinedPlayer -> Factions.player
		removeFactionMemberEvent -> Factions.neutral
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

fun selectActiveAccessoryFromHands(accessories: Hands): Id =
	accessories.mapNotNull { hand ->
		val accessory = getHandComponent<Accessory>(hand)
		if (accessory?.canBeActivated == true)
			hand.id
		else
			null
	}.firstOrNull() ?: emptyId

fun newCharacter(
	definition: CharacterDefinition,
	accessories: Hands,
	toolOffset: Vector3,
	name: String = definition.name,
	faction: Key? = null
) =
	Character(
		definition = definition,
		name = name,
		faction = faction ?: definition.faction,
		destructible = Destructible(
			health = definition.health / 2,
			maxHealth = definition.health,
			drainDuration = healthTimeDrainDuration,
		),
		depiction = definition.depiction,
		frame = definition.frame,
//    attributes = definition.attributes,
		activeAccessory = selectActiveAccessoryFromHands(accessories),
		toolOffset = toolOffset,
	)

//fun updateCharacterHealth(deck: Deck, actor: Id, characterEvents: Events, character: Character): Int {
//	val healthMod = filterEventValues<Int>(modifyHealthCommand, characterEvents)
//		.sum() +
//			applyDamage(deck, actor, characterEvents)
//
//	val healthSet = filterEventValues<Int>(setHealthCommand, characterEvents)
//		.firstOrNull()
//
//	// Directly setting health overrides any other modifiers and is only intended for special cases where the
//	// character is effectively damage immune for a frame
//	return healthSet ?: modifyResource(healthMod, character.definition.health, character.health)
//}

fun shiftActiveAction(accessories: Table<Accessory>, actor: Id, accessory: Id, offset: Int): Id {
	val actions = getActionsSequence(accessories, actor).toList()
	val index = actions.indexOfFirst { it.key == accessory }
	val rawIndex = index - offset
	val nextIndex = (rawIndex + actions.size) % actions.size
	return actions[nextIndex].key
}

fun updateActiveAccessory(accessories: Table<Accessory>, input: PlayerInput, actor: Id, activeAccessory: Id): Id =
	if (activeAccessory != emptyId && accessories.containsKey(activeAccessory))
		when (input.actionChange) {
			ActionChange.noChange -> activeAccessory
			ActionChange.previous -> shiftActiveAction(accessories, actor, activeAccessory, -1)
			ActionChange.next -> shiftActiveAction(accessories, actor, activeAccessory, 1)
		}
	else
		getActionsSequence(accessories, actor)
			.firstOrNull()
			?.key ?: emptyId

fun updateCharacter(world: World, inputs: PlayerInputs, events: Events): (Id, Character) -> Character =
	{ actor, character ->
		val deck = world.deck
		val input = inputs[actor] ?: emptyPlayerInput
		val characterEvents = events.filter { it.target == actor }
		val body = deck.bodies[actor]
		val destructible = if (body != null && body.translation.y < -50f)
			character.destructible.copy(
				health = 0,
			)
		else
			updateDestructible(events)(actor, character.destructible)

		val health = destructible.health

		val depiction = if (health == 0)
			"sprites"
		else
			character.originalDepiction

		character.copy(
			destructible = destructible,
			depiction = depiction,
//    body = updateCharacterBody(characterEvents, character.body),
			faction = updateCharacterFaction(characterEvents, character.faction),
			activeAccessory = updateActiveAccessory(deck.accessories, input, actor, character.activeAccessory),
		)
	}

fun addCharacter(
	definitions: Definitions,
	definition: CharacterDefinition,
	id: Id,
	nextId: NextId,
	characterBody: CharacterBody,
	name: String = definition.name,
	faction: Key? = null,
	additional: List<Any> = listOf()
): Hands {
	return tempCatch {
		val sprite = characterBody.findNode("sprite") as AnimatedSprite3D?
		val accessories = newCharacterAccessories(definitions, definition, id, nextId)
		val toolOffset = characterBody.toolOffset
		val character = newCharacter(definition, accessories, toolOffset, name, faction)
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
	type: String,
	faction: Key,
	name: String? = null,
	id: Id? = null,
	additional: List<Any> = listOf()
): Hands {
	val dice = world.dice
	val definitions = world.definitions
	val definition = definitions.characters[type] ?: return listOf()
	val nextId = world.nextId.source()
	val actor = id ?: nextId()

	val body = scene.instance() as CharacterBody
	body.actor = actor
	body.translation = origin + getRandomizedSpawnOffset(dice)

	body.rotation = rotation

	// The body needs to be added to the world before addCharacter because
	// Godot does not call _ready until the node is added to the scene tree
	world.scene.addChild(body)

	return addCharacter(
		definitions, definition, actor, nextId, body,
		name ?: definition.name, faction, additional
	)
}
