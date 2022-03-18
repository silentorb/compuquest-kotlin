package compuquest.clienting.multiplayer

import compuquest.clienting.Client
import compuquest.clienting.gui.Screens
import compuquest.clienting.gui.getPlayerMenuStack
import compuquest.clienting.gui.navigateBack
import compuquest.definition.playerProfessionDefinitions
import compuquest.simulation.general.*
import compuquest.simulation.input.Commands
import silentorb.mythic.debugging.getDebugString
import silentorb.mythic.haft.PlayerMap
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventTargets

fun updatePlayerMap(deck: Deck, players: List<Int>): PlayerMap =
	deck.players
		.filterValues { players.contains(it.index) }
		.mapValues { it.value.index }

fun updateClientPlayers(events: Events, players: List<Int>): List<Int> {
	val newPlayersCount = events.count { it.type == Commands.addPlayer }
	val removedPlayers = filterEventTargets<Int>(Commands.removePlayer, events)
	return if (newPlayersCount > 0 || removedPlayers.any()) {
		val pruned = players - removedPlayers
		pruned
			.plus(
				(0..3)
					.minus(pruned)
					.take(newPlayersCount)
			)
	} else
		players
}

fun addNewPlayer(playerIndex: Int): Event {
	val defaultProfession = getDebugString("DEFAULT_PLAYER_PROFESSION")
	val characterRequest = if (defaultProfession != null && playerProfessionDefinitions.containsKey(defaultProfession))
		NewPlayerCharacter(
			type = defaultProfession,
		)
	else
		null

	val request = NewPlayer(
		index = playerIndex,
		character = characterRequest,
	)
	return Event(newPlayerEvent, null, request)
}

fun newPlayerEvents(client: Client, deck: Deck): Events =
	client.players
		.filter { index -> deck.players.none { it.value.index == index } }
		.map(::addNewPlayer) +
			client.playerMap.keys
				.filter { !deck.characters.containsKey(it) && getPlayerMenuStack(client, it).none() }
				.map { Event(Commands.navigate, it, Screens.chooseProfession) }
