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
	val previousActiveAccessory: Id = emptyId,
) : SpriteState, Relational {
	val isAlive: Boolean = isCharacterAlive(health)
	val health: Int get() = destructible.health
}

fun isCharacterAlive(health: Int): Boolean =
	health > 0

fun isCharacterAlive(deck: Deck, actor: Id): Boolean =
	deck.characters[actor]?.isAlive == true

fun getActionsSequence(deck: Deck, actor: Id) =
	getOwnerAccessories(deck, actor).asSequence()
		.filter { it.value.canBeActivated }

fun hasAccessoryWithActionEffect(deck: Deck, actor: Id, effect: Key): Boolean =
	getOwnerAccessories(deck, actor).any { it.value.definition.actionEffects.any { a -> a.type == effect } }

fun hasPassiveEffect(accessories: Table<Accessory>, effect: Key): Boolean =
	accessories.any { it.value.definition.passiveEffects.any { a -> a.type == effect } }

fun hasAccessoryOfType(deck: Deck, actor: Id, type: Key): Boolean =
	getOwnerAccessories(deck, actor).any { it.value.definition.key == type }

fun getAccessoriesWithActionEffect(deck: Deck, actor: Id, effect: Key) =
	getOwnerAccessories(deck, actor).filter { it.value.definition.actionEffects.any { a -> a.type == effect } }

fun getReadyAccessories(deck: Deck, actor: Id): Table<Accessory> =
	getOwnerAccessories(deck, actor)
		.filter { canUse(it.value) }

fun canUse(accessory: Accessory): Boolean {
	return accessory.definition.actionEffects.any() && accessory.cooldown == 0f
}

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
	nextId: NextId,
	accessories: List<Key>
): Table<Accessory> =
	accessories
		.associate { accessory ->
			nextId() to newAccessory(definitions, accessory)
		}

fun selectActiveAccessoryFromTable(accessories: Table<Accessory>): Id =
	accessories.entries
		.firstOrNull { (_, accessory) ->
			accessory.canBeActivated
		}?.key ?: emptyId

fun newCharacter(
	definition: CharacterDefinition,
	accessories: Table<Accessory>,
	name: String = definition.name,
	relationships: Relationships = listOf(),
) = Character(
	definition = definition,
	name = name,
	destructible = Destructible(
		health = if (getDebugBoolean("HALF_HEALTH")) definition.health / 2 else definition.health,
		maxHealth = definition.health,
		drainDuration = healthTimeDrainDuration,
	),
	relationships = relationships,
	depiction = definition.depiction,
	frame = definition.frame,
	activeAccessory = selectActiveAccessoryFromTable(accessories),
)

fun shiftActiveAction(accessories: Table<Accessory>, accessory: Id, offset: Int): Id {
	val actions = accessories.filter { it.value.canBeActivated }.entries.toList()
	val index = actions.indexOfFirst { it.key == accessory }
	val rawIndex = index - offset
	val nextIndex = (rawIndex + actions.size) % actions.size
	return actions[nextIndex].key
}

const val equipPrevious = "equipPrevious"

fun updateActiveAccessory(
	accessories: Table<Accessory>,
	characterEvents: Events,
	input: PlayerInput,
	active: Id,
	previous: Id
): Id =
	if (active != emptyId && accessories.containsKey(active))
		when (input.actionChange) {
			ActionChange.noChange ->
				if (characterEvents.any { it.type == equipPrevious } && accessories.containsKey(previous))
					previous
				else
					active
			ActionChange.previous -> shiftActiveAction(accessories, active, -1)
			ActionChange.next -> shiftActiveAction(accessories, active, 1)
		}
	else if (accessories.containsKey(previous))
		previous
	else
		accessories.entries.firstOrNull { it.value.canBeActivated }
			?.key ?: emptyId

fun updateCharacter(world: World, inputs: PlayerInputs, events: Events): (Id, Character) -> Character =
	{ actor, character ->
		val deck = world.deck
		val input = inputs[actor] ?: emptyPlayerInput
		val actorEvents = events.filter { it.target == actor }
		val body = deck.bodies[actor]

		// Check for falling off the edge of the world
		val destructible = if (body != null && body.translation.y < -50f)
			character.destructible.copy(
				health = 0,
			)
		else {
			updateDestructible(actorEvents, character.isAlive, character.destructible)
		}

		val health = destructible.health

		val depiction = if (health == 0)
			"sprites"
		else
			character.definition.depiction

		val frame = if (health == 0)
			0
		else
			character.definition.frame

		val accessories = getOwnerAccessories(deck, actor)
		val activeAccessory = updateActiveAccessory(
			accessories,
			actorEvents,
			input,
			character.activeAccessory,
			character.previousActiveAccessory
		)

		val previousActiveAccessory = if (activeAccessory != character.activeAccessory && activeAccessory != 0L)
			activeAccessory
		else
			character.previousActiveAccessory

		character.copy(
			destructible = destructible,
			depiction = depiction,
			frame = frame,
			activeAccessory = activeAccessory,
			previousActiveAccessory = previousActiveAccessory,
		)
	}

fun addCharacter(
	definitions: Definitions,
	definition: CharacterDefinition,
	id: Id,
	nextId: NextId,
	characterBody: CharacterBody,
	name: String = definition.name,
	relationships: Relationships = listOf(),
	accessories: Table<Accessory> = mapOf(),
	additional: List<Any> = listOf()
): Hands {
	return tempCatch {
		val sprite = (characterBody as Node).findNode("sprite") as AnimatedSprite3D
		characterBody.sprite
		val allAccessories = accessories + newCharacterAccessories(definitions, nextId, definition.accessories)
		val character = newCharacter(definition, accessories, name, relationships = relationships)
		sprite.animation = character.depiction
		sprite.frame = character.frame.toLong()

		listOf(
			Hand(
				id = id,
				components = listOfNotNull(
					character,
					sprite,
					characterBody,
					AccessoryContainer(accessories = allAccessories),
				) + additional
			)
		)
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
	accessories: Table<Accessory> = mapOf(),
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
		name ?: definition.name, relationships, accessories, additional
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
	spawnCharacter(world, getAiBodyScene(), transform, type, relationships, name, id, additional = additional)

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

	return listOf(
		Hand(
			id = entity,
			components = components,
		)
	)
}
