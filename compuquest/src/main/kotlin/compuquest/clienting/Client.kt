package compuquest.clienting

import compuquest.clienting.dev.updateDev
import compuquest.clienting.gui.MenuStack
import compuquest.clienting.gui.MenuStacks
import compuquest.clienting.gui.updateMenuStacks
import compuquest.clienting.input.*
import compuquest.clienting.multiplayer.*
import compuquest.simulation.general.World
import compuquest.simulation.input.PlayerInputs
import silentorb.mythic.ent.HashedMap
import silentorb.mythic.haft.*
import silentorb.mythic.happening.Events

data class PlayerGui(
	val menuStack: MenuStack = listOf(),
)

data class Client(
	val menuStacks: MenuStacks = mapOf(),
	val options: AppOptions,
	val players: List<Int> = listOf(0),
	val playerMap: PlayerMap = mapOf(),
	val input: InputState,
	val playerInputs: PlayerInputs = mapOf(),
	val viewports: SplitViewports = listOf(), // Does not include the root viewport
)

fun newClient(): Client {
	val serializedOptions = loadOptions()
	val profiles = serializedOptions.input.profiles
	return Client(
		input = InputState(
			profileOptions = HashedMap.from(profiles),
			profiles = compileInputProfiles(profiles),
			playerProfiles = serializedOptions.input.playerProfiles,
		),
		options = AppOptions(
			audio = serializedOptions.audio,
			display = serializedOptions.display,
			ui = serializedOptions.ui,
		),
	)
}

fun restartClient(client: Client) =
	client.copy(
		menuStacks = mapOf(),
	)

fun updateClient(world: World?, events: Events, delta: Float, client: Client): Client {
	return if (world != null) {
		val deck = world.deck
		val players = updateClientPlayers(events, client.players)
		val playerMap = updatePlayerMap(deck)
		val menuStacks = updateMenuStacks(playerMap, deck, events, client.menuStacks)
		val playerInputContexts = playerMap.mapValues { (player, _) -> getPlayerInputContext(menuStacks, player) }
		val input = updateInput(delta, players, playerInputContexts, events, client.input)
		val playerInputs = newPlayerInputs(client.input, playerMap)
		updateDev()
		updateButtonPressHistory()

		client.copy(
			players = players,
			playerMap = playerMap,
			menuStacks = menuStacks,
			input = input,
			playerInputs = playerInputs,
			viewports = updateSplitScreenViewports(world, client),
		)
	} else
		client
}

fun eventsFromClient(client: Client, world: World?): Events =
	getUiCommandEvents(client) +
			if (world != null)
				newPlayerEvents(client, world)
			else
				listOf()
