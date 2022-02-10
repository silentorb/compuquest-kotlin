package compuquest.simulation.general

import compuquest.population.getDirectRelationshipAttachments
import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.characters.getCharacterGroups
import compuquest.simulation.characters.getRandomizedSpawnOffset
import compuquest.simulation.characters.spawnCharacter
import compuquest.simulation.combat.Attack
import compuquest.simulation.combat.attackEvent
import compuquest.simulation.combat.restoreFullHealthEvent
import compuquest.simulation.input.Commands
import compuquest.simulation.physics.setLocationEvent
import compuquest.simulation.updating.simulationFps
import scripts.entities.PlayerSpawner
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.handleEvents
import silentorb.mythic.happening.newEvent

const val maxPartySize = 4
const val playerFaction = "player"
const val playerRespawnTime = 5 * simulationFps
const val newPlayerEvent = "newPlayerEvent"

val hiredNpc = "hiredNpc"
val joinedPlayer = "joinedPlayer"
val addMemberToParty = "addMemberToParty"
val removeMemberFromParty = "removeMemberFromParty"

data class Player(
	val index: Int,
	val canInteractWith: Interactable? = null,
	val interactingWith: Id? = null,
	val isPlaying: Boolean = true,
	val respawnTimer: Int = 0,
)

data class NewPlayer(
	val index: Int,
	val faction: Id? = null,
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
		getPlayerRespawnPoint(world, actor) != null
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

fun getPlayerRespawnPoint(world: World, groups: Collection<Id>): PlayerSpawner? =
	world.playerSpawners
		.firstOrNull { spawner ->
			spawner.relationships
				.any { it.isA == RelationshipType.member && groups.contains(it.of) }
		} ?: world.playerSpawners.firstOrNull()

fun getPlayerRespawnPoint(world: World, actor: Id): PlayerSpawner? =
	getPlayerRespawnPoint(world, getCharacterGroups(world.deck, actor) + actor)

fun respawnPlayer(world: World, actor: Id): Events {
	val respawner = getPlayerRespawnPoint(world, actor)
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

fun spawnNewPlayer(world: World, playerIndex: Int, faction: Id): Hands {
	val spawner = getPlayerRespawnPoint(world, faction)
	val scene = spawner?.scene
	return if (scene != null && world.deck.players.none { it.value.index == playerIndex }) {
		val nextId = world.nextId.source()
		val actor = nextId()
		val name = newPlayerName(playerIndex)
		spawnCharacter(
			world,
			scene,
			spawner.globalTransform.origin,
			spawner.rotation,
			spawner.type,
			spawner.relationships,
			name,
			actor
		) +
				listOf(
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
		listOf()
}
