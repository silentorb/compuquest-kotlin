package compuquest.simulation.general

import compuquest.simulation.characters.getRandomizedSpawnOffset
import compuquest.simulation.characters.setHealthCommand
import compuquest.simulation.characters.spawnCharacter
import compuquest.simulation.combat.Attack
import compuquest.simulation.combat.attackEvent
import compuquest.simulation.input.Commands
import compuquest.simulation.physics.setLocationEvent
import compuquest.simulation.updating.simulationFps
import scripts.entities.PlayerSpawner
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.handleEvents

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
	val faction: Key,
//  val party: List<Id> = listOf(),
	val canInteractWith: Interactable? = null,
	val interactingWith: Id? = null,
	val isPlaying: Boolean = true,
	val respawnTimer: Int = 0,
)

data class NewPlayer(
	val index: Int,
	val faction: Key? = null,
)

fun updateInteractingWith(player: Player) = handleEvents<Id?> { event, value ->
	when (event.type) {
		Commands.interact -> player.canInteractWith?.target
		Commands.finishInteraction -> null
		else -> value
	}
}

fun updateParty() = handleEvents<List<Id>> { event, value ->
	when (event.type) {
		hiredNpc -> value + event.value as Id
		addMemberToParty -> {
			val member = event.value as? Id
			if (member != null && value.size < maxPartySize)
				value + member
			else
				value
		}
		removeMemberFromParty -> {
			val member = event.value as? Id
			if (member != null && value.size > 1)
				value - member
			else
				value
		}
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

//	val removedCharacters = events.filter { it.type == removeFactionMemberEvent }
//		.mapNotNull {
//			if (deck.characters[it.target]?.faction == player.faction)
//				it.target as? Id
//			else
//				null
//		}

	val interactingWith = if (isPlaying)
		updateInteractingWith(player)(events.filter { it.target == actor }, player.interactingWith)
	else
		null

	val respawnTimer = if (
		!isAlive &&
		character != null &&
		world.scenario.playerRespawning &&
		getPlayerRespawnPoint(world, character.faction) != null
	)
		player.respawnTimer + 1
	else
		0

//  val party = updateParty()(playerEvents, player.party) - removedCharacters

//  val isPlaying = party.any { deck.characters[it]?.isAlive ?: false }

//  val menu = if (!isPlaying && player.isPlaying)
//    gameOverScreen
//  else
//    updateManagementMenu(events, player.menu)

	player.copy(
		canInteractWith = canInteractWith,
		interactingWith = interactingWith,
//    party = party,
		isPlaying = isPlaying,
		respawnTimer = respawnTimer,
	)
}

//fun getNonPartyMembers(deck: Deck, player: Player): Set<Id> =
//  deck.characters
//    .filterValues { it.faction == player.faction }
//    .keys - player.party

fun getPlayerRespawnPoint(world: World, faction: Key): PlayerSpawner? =
	world.playerSpawners.firstOrNull { it.faction == faction }

fun respawnPlayer(world: World, actor: Id): Events {
	val deck = world.deck
	val character = deck.characters[actor]!!
	val respawner = getPlayerRespawnPoint(world, character.faction)
	return if (respawner != null) {
		val dice = world.dice
		val location = respawner.globalTransform.origin + getRandomizedSpawnOffset(dice)
		listOf(
			Event(setHealthCommand, actor, character.definition.health),
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

fun spawnNewPlayer(world: World, playerIndex: Int, faction: Key): Hands {
	val spawner = getPlayerRespawnPoint(world, faction)
	val scene = spawner?.scene
	return if (scene != null && world.deck.players.none { it.value.index == playerIndex }) {
		val nextId = world.nextId.source()
		val actor = nextId()
		val name = newPlayerName(playerIndex)
		spawnCharacter(world, scene, spawner.globalTransform.origin, spawner.rotation, spawner.type, faction, name, actor) +
				listOf(
					Hand(
						id = actor,
						components = listOf(
							Player(
								index = playerIndex,
								faction = faction,
							)
						)
					)
				)
	} else
		listOf()
}
