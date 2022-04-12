package compuquest.clienting

import compuquest.clienting.audio.AudioPlayerPool
import compuquest.clienting.audio.updateAudio
import compuquest.clienting.gui.*
import compuquest.clienting.input.*
import compuquest.clienting.multiplayer.*
import compuquest.population.MaterialMap
import compuquest.simulation.general.World
import compuquest.simulation.input.PlayerInputs
import scripts.Global
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
	val players: List<Int> = listOf(0),
	val playerMap: PlayerMap = mapOf(),
	val input: InputState,
	val playerInputs: PlayerInputs = mapOf(),
	val viewports: SplitViewports = listOf(), // Does not include the root viewport
	val materials: MaterialMap = mutableMapOf(),
	val engagedAccessories: Map<Id, Id> = mapOf(), // Contains the ids of each player who has used their active accessory since last selected
	val audioPool: AudioPlayerPool = AudioPlayerPool(),
	val characterCreationStates: Map<Id, CharacterCreation> = mapOf(),
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
		playerMap = mapOf(),
		viewports = listOf(),
	)

fun updateClientWithWorld(world: World, events: Events, delta: Float, client: Client): Client {
	val deck = world.deck
	val players = updateClientPlayers(events, client.players)
	val playerMap = updatePlayerMap(deck, players)
	val activeAccessories = playerMap
		.mapValues { (player, _) ->
			world.deck.characters[player]?.primaryAccessory ?: 0L
		}

	val prunedEngagedAccessories = client.engagedAccessories
		.filter { (player, accessory) ->
			activeAccessories[player] == accessory
		}

	val playerInputContexts = playerMap.mapValues { (player, _) -> getPlayerInputContext(client.menuStacks, player) }
	val input = updateInput(delta, players, playerInputContexts, events, client.input)
	val playerInputs = newPlayerInputs(client.menuStacks, client.input, playerMap, prunedEngagedAccessories)
	val populatedEngagedAccessories = activeAccessories.filter { (player, _) ->
		playerInputs[player]?.primaryAction == true || prunedEngagedAccessories.contains(player)
	}

	val scenario = world.scenario
	val characterCreationStates = updateCharacterCreationStates(scenario, deck, events, client.characterCreationStates)

	syncGodotUiEvents(playerMap, client.menuStacks, input)
	updateCustomInputHandlers(playerMap, client.menuStacks, input)
	updateAudio(world.scene, client.audioPool, world.previousEvents)

	// updating menuStacks is one of the last steps to make sure the rest of the client code
	// is considering the current menu and not the next menu that hasn't been displayed yet
	val menuStacks = updateMenuStacks(playerMap, deck, events, client.menuStacks)

	return client.copy(
		players = players,
		playerMap = playerMap,
		menuStacks = menuStacks,
		input = input,
		playerInputs = playerInputs,
		viewports = updateSplitScreenViewports(world, playerMap, client.viewports),
		engagedAccessories = populatedEngagedAccessories,
		characterCreationStates = characterCreationStates,
	)
}

fun updateClient(world: World?, events: Events, delta: Float, client: Client): Client =
	if (world != null)
		updateClientWithWorld(world, events, delta, client)
	else
		client

fun serverEventsFromClient(client: Client, world: World?, events: Events): Events =
	getUiCommandEvents(client) +
			if (world != null)
				newPlayerEvents(client, world.deck) +
						characterCreationNavigation(client, world.scenario, world.deck, events)
			else
				listOf()

fun isPrimaryPlayer(playerMap: PlayerMap, player: Id): Boolean =
	playerMap[player] == 0
