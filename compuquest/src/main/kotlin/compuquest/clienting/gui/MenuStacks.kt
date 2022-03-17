package compuquest.clienting.gui

import compuquest.clienting.Client
import compuquest.simulation.general.Deck
import silentorb.mythic.ent.Id
import silentorb.mythic.haft.PlayerMap
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventTargets
import silentorb.mythic.happening.filterEventsByTarget

fun getPlayerMenuStack(client: Client, player: Id): MenuStack =
	client.menuStacks[player] ?: listOf()

fun updateMenuStacks(players: PlayerMap, deck: Deck, events: Events, menuStacks: MenuStacks): MenuStacks {
	// Temporarily restrict the game to only allow one player menu at a time.
	// Will eventually be removed when either Godot supports multiplayer UI focus
	// or custom focus management is implemented
	val currentMenu = menuStacks.entries.firstOrNull { it.value.any() }?.key
	return players
		.mapValues { (actor, _) ->
			val player = deck.players[actor]
			if (player != null) //  && (currentMenu == null || currentMenu == actor)
				updateMenuStack(player)(filterEventsByTarget(actor, events), menuStacks[actor] ?: listOf())
			else
				listOf()
		}
}
