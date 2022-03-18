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
import compuquest.simulation.intellect.knowledge.Personality
import godot.AnimatedSprite3D
import godot.Node
import godot.PackedScene
import godot.core.Transform
import godot.core.Vector3
import godot.global.GD
import scripts.entities.CharacterBody
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.ent.*
import silentorb.mythic.godoting.tempCatch
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.handleEvents
import silentorb.mythic.randomly.Dice
import silentorb.mythic.timing.newTimer

data class CharacterDefinition(
	val key: Key,
	val name: String = key,
	val attributes: Set<Key> = setOf(),
	val depiction: String,
	val frame: Int = 0,
	val health: Int,
	val accessories: List<Key> = listOf(),
	val personality: Personality? = null,
	val speed: Float = 10f,
)

data class Character(
	val definition: CharacterDefinition,
	val name: String,
	val destructible: Destructible,
	val attributes: Set<Key> = setOf(),
	val fee: Int = 0,
	val enemyVisibilityRange: Float = 0f,
	override val relationships: Relationships = listOf(),
	override val depiction: String,
	override val frame: Int = 0,
	val activeAccessory: Id = emptyId,
	val toolOffset: Vector3 = Vector3.ZERO,
) : SpriteState, Relational {
	val isAlive: Boolean = isCharacterAlive(health)
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

fun getAccessoriesWithEffect(accessories: Table<Accessory>, actor: Id, effect: Key) =
	getOwnerAccessories(accessories, actor).filter { it.value.definition.actionEffects.any { a -> a.type == effect } }

fun getReadyAccessories(deck: Deck, actor: Id): Table<Accessory> =
	getOwnerAccessories(deck.accessories, actor)
		.filter { canUse(it.value) }

fun canUse(accessory: Accessory): Boolean {
	return accessory.definition.actionEffects.any() && accessory.cooldown == 0f
}

fun canUse(world: World, accessory: Id): Boolean =
	canUse(world.deck.accessories[accessory]!!)

fun eventsFromCharacter(previous: World): (Id, Character) -> Events = { actor, character ->
	val deck = previous.deck
	val a = deck.characters[actor]
	if (a?.isAlive == true && !character.isAlive && !deck.players.containsKey(actor))
		listOf(newHandEvent(Hand(id = actor, components = listOf(newTimer(10f)))))
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
	relationships: Relationships = listOf(),
) =
	Character(
		definition = definition,
		name = name,
		destructible = Destructible(
			health = if (getDebugBoolean("HALF_HEALTH")) definition.health / 2 else definition.health,
			maxHealth = definition.health,
			drainDuration = healthTimeDrainDuration,
		),
		depiction = definition.depiction,
		frame = definition.frame,
		activeAccessory = selectActiveAccessoryFromHands(accessories),
		relationships = relationships,
		toolOffset = toolOffset,
	)

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
			character.definition.depiction

		val frame = if (health == 0)
			0
		else
			character.definition.frame

		val accessories = deck.accessories.filter { it.value.owner == actor }

		character.copy(
			destructible = destructible,
			depiction = depiction,
			frame = frame,
			activeAccessory = updateActiveAccessory(accessories, input, actor, character.activeAccessory),
		)
	}

fun addCharacter(
	definitions: Definitions,
	definition: CharacterDefinition,
	id: Id,
	nextId: NextId,
	characterBody: CharacterBody,
	name: String = definition.name,
	relationships: Relationships,
	additional: List<Any> = listOf()
): Hands {
	return tempCatch {
		val sprite = (characterBody as Node).findNode("sprite") as AnimatedSprite3D
		characterBody.sprite
		val accessories = newCharacterAccessories(definitions, definition, id, nextId)
		val toolOffset = characterBody.toolOffset
		val character = newCharacter(definition, accessories, toolOffset, name, relationships = relationships)
		sprite.animation = character.depiction
		sprite.frame = character.frame.toLong()

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
	world: PreWorld,
	scene: PackedScene,
	transform: Transform,
	type: String,
	relationships: Relationships = listOf(),
	name: String? = null,
	id: Id? = null,
	additional: List<Any> = listOf()
): Hands {
	val definitions = world.definitions
	val definition = definitions.characters[type] ?: return listOf()
	val nextId = world.nextId.source()
	val actor = id ?: nextId()

	println("*** ${type}")
	val body: CharacterBody = try {
		val node = scene.instance()
		node as? CharacterBody
	} catch (e: Throwable) {
		throw Error("Error instantiating character scene")
	} ?: return listOf()

	body.actor = actor
	body.globalTransform = transform
	body.walkSpeed = definition.speed
	body.speed = definition.speed

	// The body needs to be added to the world before addCharacter because
	// Godot does not call _ready until the node is added to the scene tree
	world.scene.addChild(body as Node)

	return addCharacter(
		definitions, definition, actor, nextId, body,
		name ?: definition.name, relationships, additional
	)
}

fun getAiBodyScene() =
	GD.load<PackedScene>("res://entities/actor/ActorBodyCapsuleRigid.tscn")!!

fun spawnAiCharacter(
	world: PreWorld,
	transform: Transform,
	type: String,
	relationships: Relationships = listOf(),
	name: String? = null,
	id: Id? = null,
	additional: List<Any> = listOf()
): Hands =
	spawnCharacter(world, getAiBodyScene(), transform, type, relationships, name, id, additional)

fun getCharacterGroupRelationships(deck: Deck, character: Character): Collection<Relationship> =
	character.relationships
		.filter { it.isA == RelationshipType.member } +
			character.relationships
				.filter { it.isA == RelationshipType.master }
				.flatMap {
					val master = deck.characters[it.of]
					if (master != null)
						getCharacterGroupRelationships(deck, master)
					else
						listOf()
				}

fun getCharacterGroups(deck: Deck, character: Character): Collection<Id> =
	getCharacterGroupRelationships(deck, character)
		.map { it.of }

fun getCharacterGroups(deck: Deck, actor: Id): Collection<Id> {
	val character = deck.characters[actor]
	return if (character != null)
		getCharacterGroups(deck, character).toList()
	else
		listOf()
}

fun copyEntity(deck: Deck, entity: Id): Hands {
	val components = deckReflection.deckProperties.mapNotNull { property ->
		val table = property.get(deck) as Table<Any>
		table[entity]
	}

	val accessories = getOwnerAccessories(deck.accessories, entity)
		.keys
		.flatMap { copyEntity(deck, it) }

	return listOf(
		Hand(
			id = entity,
			components = components,
		)
	) + accessories
}
