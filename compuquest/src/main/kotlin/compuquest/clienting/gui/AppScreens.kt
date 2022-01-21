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
