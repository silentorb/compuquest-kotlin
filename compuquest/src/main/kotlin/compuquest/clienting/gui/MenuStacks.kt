package compuquest.clienting.gui

import compuquest.clienting.Client
import compuquest.simulation.general.Deck
import silentorb.mythic.ent.Id
import silentorb.mythic.haft.PlayerMap
import silentorb.mythic.happening.Events

fun getPlayerMenuStack(client: Client, player: Id): MenuStack =
	client.menuStacks[player] ?: listOf()

fun updateMenuStacks(players: PlayerMap, deck: Deck, events: Events, menuStacks: MenuStacks): MenuStacks =
	players
		.mapValues { (actor, _) ->
			val player = deck.players[actor]
			if (player != null)
				updateMenuStack(player)(events, menuStacks[actor] ?: listOf())
			else
				listOf()
		}
