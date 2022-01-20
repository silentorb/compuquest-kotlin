package compuquest.clienting

import compuquest.clienting.dev.updateDev
import compuquest.clienting.display.SplitViewports
import compuquest.clienting.display.updateSplitScreenViewports
import compuquest.clienting.gui.MenuStack
import compuquest.clienting.gui.updateMenuStack
import compuquest.clienting.input.defaultInputProfile
import compuquest.clienting.input.defaultInputProfiles
import compuquest.clienting.input.newPlayerInputs
import compuquest.simulation.general.World
import compuquest.simulation.general.getPlayer
import compuquest.simulation.input.Commands
import compuquest.simulation.input.PlayerInputs
import silentorb.mythic.ent.Id
import silentorb.mythic.haft.InputState
import silentorb.mythic.haft.updateButtonPressHistory
import silentorb.mythic.haft.updateInput
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

data class PlayerGui(
	val menuStack: MenuStack = listOf(),
)

data class Client(
	val menuStack: MenuStack = listOf(),
	val options: AppOptions,
	val input: InputState = InputState(
		profiles = defaultInputProfiles(),
		playerProfiles = mapOf(1L to defaultInputProfile)
	),
	val playerInputs: PlayerInputs = mapOf(),
	val viewports: SplitViewports = listOf(), // Does not include the root viewport
)

fun newClient() =
	Client(
		options = loadOptions(),
	)

fun restartClient(client: Client) =
	client.copy(
		menuStack = listOf(),
	)

fun getPlayerMenuStack(client: Client, player: Id): MenuStack =
	client.menuStack

fun updateClient(world: World?, events: Events, delta: Float, client: Client): Client {
	return if (world != null) {
		val player = getPlayer(world)
		val players = world.deck.players
		val input = updateInput(delta, players.keys, client.input)
		val playerInputs = newPlayerInputs(client.input, players.keys)
		updateDev()
		updateButtonPressHistory()

		client.copy(
			// TODO: Change menuStack to be multiplayer-friendly
			menuStack = if (player != null)
				updateMenuStack(player.value)(events, client.menuStack)
			else
				client.menuStack,
			input = input,
			playerInputs = playerInputs,
			viewports = updateSplitScreenViewports(world, client),
		)
	} else
		client
}

fun eventsFromClient(player: Id, client: Client, previous: Client?): Events =
	if (previous == null)
		listOf()
	else
		listOfNotNull(
			if (client.menuStack.none() and previous.menuStack.any())
				newEvent(Commands.finishInteraction, player)
			else
				null
		)
