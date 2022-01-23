package compuquest.clienting.multiplayer

import compuquest.clienting.Client
import compuquest.simulation.general.Deck
import compuquest.simulation.general.NewPlayer
import compuquest.simulation.general.World
import compuquest.simulation.general.newPlayerEvent
import compuquest.simulation.input.Commands
import silentorb.mythic.haft.PlayerMap
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventTargets

fun updatePlayerMap(deck: Deck): PlayerMap =
	deck.players.mapValues { it.value.index }

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

fun newPlayerEvents(client: Client, world: World): Events =
	client.players
		.filter { index -> world.deck.players.none { it.value.index == index } }
		.map { playerIndex ->
			Event(newPlayerEvent, null, NewPlayer(index = playerIndex))
		}
