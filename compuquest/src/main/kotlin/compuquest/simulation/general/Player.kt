package compuquest.simulation.general

import compuquest.simulation.characters.*
import compuquest.simulation.combat.Attack
import compuquest.simulation.combat.attackEvent
import compuquest.simulation.combat.restoreFullHealthEvent
import compuquest.simulation.input.Commands
import compuquest.simulation.physics.setLocationEvent
import compuquest.simulation.updating.simulationFps
import godot.core.Vector3
import scripts.entities.PlayerSpawner
import silentorb.mythic.debugging.getDebugString
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.emptyId
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.handleEvents
import silentorb.mythic.happening.newEvent

const val playerRespawnTime = 5 * simulationFps
const val newPlayerEvent = "newPlayer"
const val newPlayerCharacterEvent = "newPlayerCharacter"

val hiredNpc = "hiredNpc"
val joinedPlayer = "joinedPlayer"

data class Player(
	val index: Int,
	val canInteractWith: Interactable? = null,
	val interactingWith: Id? = null,
	val isPlaying: Boolean = true,
	val respawnTimer: Int = 0,
)

data class NewPlayerCharacter(
	val type: Key,
	val faction: Id? = null,
)

data class NewPlayer(
	val index: Int,
	val character: NewPlayerCharacter? = null,
)

fun updateInteractingWith(player: Player) = handleEvents<Id?> { event, value ->
	when (event.type) {
		Commands.interact -> player.canInteractWith?.target
		Commands.finishInteraction -> null
		else -> value
	}
}

fun shouldRefreshPlayerSlowdown(actor: Id, events: Events): Boolean =
	events.any { it.type == attackEvent && it.value is Attack && it.value.targetEntity == actor }

fun updatePlayer(world: World, events: Events, delta: Float): (Id, Player) -> Player = { actor, player ->
	val deck = world.deck
	val character = deck.characters[actor]
	val isAlive = character?.isAlive == true
	val isPlaying = isAlive
	val canInteractWith = if (player.interactingWith == null && isPlaying)
		getInteractable(world, actor)
	else
		null

	val interactingWith = if (isPlaying)
		updateInteractingWith(player)(events.filter { it.target == actor }, player.interactingWith)
	else
		null

	val respawnTimer = if (
		!isAlive &&
		character != null &&
		world.scenario.playerRespawning &&
		getPlayerRespawnPoint(world.playerSpawners, world.deck, actor) != null
	)
		player.respawnTimer + 1
	else
		0

	player.copy(
		canInteractWith = canInteractWith,
		interactingWith = interactingWith,
		isPlaying = isPlaying,
		respawnTimer = respawnTimer,
	)
}

fun getPlayerRespawnPoint(playerSpawners: List<PlayerSpawner>, groups: Collection<Id>): PlayerSpawner? =
	playerSpawners
		.firstOrNull { spawner ->
			spawner.relationships
				.any { it.isA == RelationshipType.member && groups.contains(it.of) }
		} ?: playerSpawners.firstOrNull()

fun getPlayerRespawnPoint(playerSpawners: List<PlayerSpawner>, deck: Deck, actor: Id): PlayerSpawner? =
	getPlayerRespawnPoint(playerSpawners, getCharacterGroups(deck, actor))

fun respawnPlayer(world: World, actor: Id): Events {
	val respawner = getPlayerRespawnPoint(world.playerSpawners, world.deck, actor)
	return if (respawner != null) {
		val dice = world.dice
		val location = respawner.globalTransform.origin + getRandomizedSpawnOffset(dice)
		listOf(
			newEvent(restoreFullHealthEvent, actor),
			Event(setLocationEvent, actor, location),
		)
	} else
		listOf()
}

fun eventsFromPlayer(world: World): (Id, Player) -> Events = { actor, player ->
	if (player.respawnTimer >= playerRespawnTime)
		respawnPlayer(world, actor)
	else
		listOf()
}

fun newPlayerName(index: Int): String =
	"Player ${index + 1}"

fun spawnNewPlayerCharacter(world: World, actor: Id, playerIndex: Int, faction: Id, type: String): Hands {
	val spawner = getPlayerRespawnPoint(world.playerSpawners, listOf(faction))
	val scene = spawner?.scene
	val deck = world.deck
	return if (scene != null && !deck.characters.containsKey(actor)) {
		val nextId = world.nextId.source()
		val name = newPlayerName(playerIndex)
		val debugAccessories = newCharacterAccessories(
			world.definitions, nextId,
			getDebugString("PLAYER_ITEMS")?.split(",") ?: listOf()
		)

		val relationships = if (spawner.relationships.any())
			spawner.relationships
		else
			listOf(Relationship(RelationshipType.member, faction))

		spawnCharacter(
			world,
			scene,
			spawner.globalTransform,
			type,
			relationships,
			name,
			actor,
			accessories = debugAccessories
		)
	} else
		listOf()
}

fun spawnNewPlayerCharacter(world: World, actor: Id, playerIndex: Int, request: NewPlayerCharacter): Events {
	val faction = request.faction
		?: getGroupByKey(world.deck.groups, world.scenario.defaultPlayerFaction)?.key

	return if (faction != null)
		newHandEvents(spawnNewPlayerCharacter(world, actor, playerIndex, faction, request.type))
	else
		listOf()
}

fun spawnNewPlayer(world: World, playerIndex: Int): Pair<Id, Hands> {
	return if (world.deck.players.none { it.value.index == playerIndex }) {
		val nextId = world.nextId.source()
		val actor = nextId()
		actor to listOf(
			Hand(
				id = actor,
				components = listOf(
					Player(
						index = playerIndex,
					)
				)
			)
		)
	} else
		emptyId to listOf()
}
