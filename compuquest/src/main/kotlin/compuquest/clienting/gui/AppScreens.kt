package compuquest.clienting.gui

import compuquest.definition.playerProfessionDefinitions
import compuquest.simulation.general.NewPlayerCharacter
import compuquest.simulation.general.deleteEntityCommand
import compuquest.simulation.general.newPlayerCharacterEvent
import compuquest.simulation.input.Commands
import scripts.gui.PlayerInputProfiles
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.newEvent

fun mainMenu() =
	GameScreen(
		title = staticTitle("Main Menu"),
		content = { context, _ ->
			val actor = context.actor
			val deck = context.world.deck
			val isPrimaryPlayer = deck.players[actor]?.index == 0
			newPopupMenu(
				"Main Menu",
				actor,
				listOfNotNull(
					GameMenuItem(
						title = "Continue",
					),
					if (isPrimaryPlayer)
						GameMenuItem(
							address = MenuAddress(Screens.options),
						)
					else
						GameMenuItem(
							title = "Leave",
							events = { context2 ->
								val player = context2.world.deck.players[context2.actor]!!
								listOf(
									newEvent(Commands.removePlayer, player.index),
									newEvent(deleteEntityCommand, actor),
								)
							}
						),
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

fun chooseProfessionMenu() =
	GameScreen(
		title = staticTitle("Choose Profession"),
		content = { context, _ ->
			val world = context.world
			val deck = world.deck
			val availableProfessions = if (getDebugBoolean("ALLOW_SAME_PLAYER_PROFESSIONS"))
				playerProfessionDefinitions
			else
				playerProfessionDefinitions - deck.players.keys.mapNotNull { deck.characters[it]?.definition?.key }

			val address = if (world.scenario.characterCustomization)
				MenuAddress(Screens.equipCharacter)
			else
				null

			newPopupMenu(
				"Choose Profession",
				context.actor,
				availableProfessions.values
					.map { definition ->
						GameMenuItem(
							title = definition.name,
							events = { context2 ->
								val request = NewPlayerCharacter(
									type = definition.key,
								)
								listOf(
									newEvent(newPlayerCharacterEvent, context2.actor, request),
								)
							},
							address = address,
						)
					}
			)
		}
	)
