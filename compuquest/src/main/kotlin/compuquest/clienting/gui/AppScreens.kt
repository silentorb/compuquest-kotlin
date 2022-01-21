package compuquest.clienting.gui


fun mainMenu() =
	GameScreen(
		title = staticTitle("Main Menu"),
		content = { _, _ ->
			newPopupMenu(
				listOf(
					GameMenuItem(
						title = "Continue",
					),
					GameMenuItem(
						title = "Options",
						address = MenuAddress(Screens.options),
					),
				)
			)
		}
	)
