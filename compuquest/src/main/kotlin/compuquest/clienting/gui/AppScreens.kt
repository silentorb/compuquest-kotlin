package compuquest.clienting.gui

fun mainMenu() =
	GameScreen(
		title = staticTitle("Main Menu"),
		content = { context, _ ->
			newPopupMenu(
				"Main Menu",
				context.actor,
				listOf(
					GameMenuItem(
						title = "Continue",
					),
					GameMenuItem(
						address = MenuAddress(Screens.options),
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
		title = staticTitle("Input Player Profiles"),
		content = { context, _ ->
			newPopupMenu(
				"Input Player Profiles",
				context.actor,
				listOf(

				)
			)
		}
	)
