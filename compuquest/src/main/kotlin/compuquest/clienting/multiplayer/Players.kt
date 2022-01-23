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

fun updatePlayerMap(deck: Deck): PlayerMap =
	deck.players.mapValues { it.value.index }

fun updateClientPlayers(events: Events, players: List<Int>): List<Int> =
	if (players.size == 4)
		players
	else {
		val newPlayersCount = events.count { it.type == Commands.addPlayer }
		if (newPlayersCount > 0)
			players
				.plus(
					(0..3)
						.minus(players)
						.take(newPlayersCount)
				)
		else
			players
	}

fun newPlayerEvents(client: Client, world: World): Events =
	client.players
		.filter { index -> world.deck.players.none { it.value.index == index } }
		.map { playerIndex ->
			Event(newPlayerEvent, null, NewPlayer(index = playerIndex))
		}
