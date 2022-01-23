package compuquest.clienting.gui

import compuquest.simulation.general.deleteEntityCommand
import compuquest.simulation.input.Commands
import scripts.gui.PlayerInputProfiles
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.newEvent

fun mainMenu() =
	GameScreen(
		title = staticTitle("Main Menu"),
		content = { context, _ ->
			val actor = context.actor
			val deck = context.world.deck
			newPopupMenu(
				"Main Menu",
				actor,
				listOfNotNull(
					GameMenuItem(
						title = "Continue",
					),
					GameMenuItem(
						address = MenuAddress(Screens.options),
					),
					if (deck.players[actor]?.index != 0)
						GameMenuItem(
							title = "Leave",
							events = { context2 ->
								val player = context2.world.deck.players[context2.actor]!!
								listOf(
									newEvent(Commands.removePlayer, player.index),
									newEvent(deleteEntityCommand, actor),
								)
							}
						)
					else
						null,
				)
			)
		}
	)

fun optionsMenu() =
	GameScreen(
		title = staticTitle("Options"),
		content = { context, _ ->
			newPopupMenu(
				"Options Menu",
				context.actor,
				listOf(
					GameMenuItem(
						title = "Display",
						address = MenuAddress(Screens.optionsDisplay),
					),
					GameMenuItem(
						title = "Input",
						address = MenuAddress(Screens.optionsInput),
					),
					GameMenuItem(
						title = "Audio",
						address = MenuAddress(Screens.optionsAudio),
					),
				)
			)
		}
	)

fun optionsInputMenu() =
	GameScreen(
		title = staticTitle("Input"),
		content = { context, _ ->
			newPopupMenu(
				"Input",
				context.actor,
				listOf(
					GameMenuItem(
						address = MenuAddress(Screens.optionsInputProfiles),
					),
					GameMenuItem(
						address = MenuAddress(Screens.optionsInputPlayerProfiles),
					),
				)
			)
		}
	)

fun optionsInputProfilesMenu() =
	GameScreen(
		title = staticTitle("Input Profiles"),
		content = { context, _ ->
			newPopupMenu(
				"Input Profiles",
				context.actor,
				listOf(

				)
			)
		}
	)

fun optionsInputPlayerProfilesMenu() =
	GameScreen(
		title = staticTitle("Player Input Profiles"),
		content = { context, _ ->
			val control = instantiateScene<PlayerInputProfiles>("res://gui/menus/PlayerInputProfiles.tscn")!!
			control.actor = context.actor
			control
		}
	)
