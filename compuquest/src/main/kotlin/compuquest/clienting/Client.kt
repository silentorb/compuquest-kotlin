package compuquest.clienting

import compuquest.clienting.dev.updateDev
import compuquest.clienting.gui.MenuStack
import compuquest.clienting.gui.MenuStacks
import compuquest.clienting.gui.updateMenuStack
import compuquest.clienting.gui.updateMenuStacks
import compuquest.clienting.input.*
import compuquest.clienting.multiplayer.SplitViewports
import compuquest.clienting.multiplayer.updateClientPlayers
import compuquest.clienting.multiplayer.updateSplitScreenViewports
import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import compuquest.simulation.input.PlayerInputs
import silentorb.mythic.ent.HashedMap
import silentorb.mythic.ent.Id
import silentorb.mythic.haft.*
import silentorb.mythic.happening.Events

data class PlayerGui(
	val menuStack: MenuStack = listOf(),
)

data class Client(
	val menuStacks: MenuStacks = mapOf(),
	val options: AppOptions,
	val players: PlayerMap = mapOf(),
	val input: InputState,
	val playerInputs: PlayerInputs = mapOf(),
	val viewports: SplitViewports = listOf(), // Does not include the root viewport
)

fun newClient(): Client {
	val profileOptions = defaultInputProfiles()
	return Client(
		input = InputState(
			profileOptions = HashedMap.from(profileOptions),
			profiles = compileInputProfiles(profileOptions),
			playerProfiles = defaultPlayerInputProfiles(),
		),
		options = loadOptions(),
	)
}

fun restartClient(client: Client) =
	client.copy(
		menuStacks = mapOf(),
	)

fun updateClient(world: World?, events: Events, delta: Float, client: Client): Client {
	return if (world != null) {
		val deck = world.deck
		val players = updateClientPlayers(deck.players.keys, client.players)
		val menuStacks = updateMenuStacks(players, deck, events, client.menuStacks)
		val playerInputContexts = players.mapValues { (player, _) -> getPlayerInputContext(menuStacks, player) }
		val input = updateInput(delta, players, playerInputContexts, client.input)
		val playerInputs = newPlayerInputs(client.input, players)
		updateDev()
		updateButtonPressHistory()

		client.copy(
			players = players,
			menuStacks = menuStacks,
			input = input,
			playerInputs = playerInputs,
			viewports = updateSplitScreenViewports(world, client),
		)
	} else
		client
}

fun eventsFromClient(client: Client): Events =
	getUiCommandEvents(client)
