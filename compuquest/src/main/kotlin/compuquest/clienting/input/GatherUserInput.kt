package compuquest.clienting.input

import compuquest.clienting.Client
import compuquest.clienting.gui.MenuStacks
import compuquest.simulation.input.*
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.haft.*
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

fun getPlayerProfile(state: InputState, playerIndex: Int): InputProfile? =
	state.profiles[state.playerProfiles[playerIndex]]

val uiCommands = listOf(
	Commands.activate,
	Commands.navigate,
	Commands.drillDown,
	Commands.menuBack,
	Commands.newGame,
	Commands.addPlayer,
)

val gameCommands = listOf(
	Commands.navigate,
	Commands.newGame,
	Commands.addPlayer,
	Commands.nextLevel,
)

fun getUiCommandEvents(commands: List<String>, bindings: Bindings, gamepad: Int, player: Id): Events =
	commands.flatMap { command ->
		getJustPressedBindings(bindings, gamepad, command)
			.map { binding ->
				newEvent(command, player, binding.argument)
			}
	}

fun getPlayerBindings(state: InputState, player: Id, playerIndex: Int): Bindings? {
	val context = state.playerInputContexts[player]
	return if (context != null)
		getPlayerProfile(state, playerIndex)?.bindings?.getOrDefault(context, null)
	else
		null
}

fun getUiCommands(playerInputContexts: Map<Id, Key>, player: Id): List<String> {
	val context = playerInputContexts[player]
	return if (context != null) {
		if (context == InputContexts.ui)
			uiCommands
		else
			gameCommands
	} else
		listOf()
}

fun getUiCommandEvents(state: InputState, player: Id, playerIndex: Int): Events {
	val bindings = getPlayerBindings(state, player, playerIndex)
	return if (bindings != null) {
		val gamepad = getPlayerGamepad(state, playerIndex)
		val commands = getUiCommands(state.playerInputContexts, player)
		getUiCommandEvents(commands, bindings, gamepad, player)
	} else
		listOf()
}

val gamepadJoinGameButtons = listOf(
	GamepadChannels.JOY_XBOX_A.toInt(),
	GamepadChannels.JOY_XBOX_B.toInt(),
	GamepadChannels.JOY_XBOX_X.toInt(),
	GamepadChannels.JOY_XBOX_Y.toInt(),
	GamepadChannels.JOY_SELECT.toInt(),
	GamepadChannels.JOY_START.toInt(),
)

fun newPlayerGamepadEvents(input: InputState): Events =
	if (input.playerGamepads.size < 4)
		input.gamepads
			.flatMap { gamepad ->
				if (
					!input.playerGamepads.values.contains(gamepad) &&
					gamepadJoinGameButtons.any { channel ->
						// Any empty list is passed for bindings because bindings are only
						// needed for axis processing and new player joining events are
						// only triggered by gamepad button events, not axis events
						isGamepadButtonJustPressed(listOf(), gamepad, channel)
					}
				)
					listOf(
						newEvent(Commands.addPlayer, null),
						newEvent(setPlayerGamepad, null, gamepad),
					)
				else
					listOf()
			}
	else
		listOf()

fun getUiCommandEvents(client: Client): Events =
	client.playerMap
		.flatMap { (player, index) ->
			getUiCommandEvents(client.input, player, index)
		} +
			newPlayerGamepadEvents(client.input)

object StandardAxisCommands {
	val lookX = AxisCommands(Commands.lookX, Commands.lookDown, Commands.lookUp)
	val lookY = AxisCommands(Commands.lookY, Commands.lookLeft, Commands.lookRight)
	val moveLengthwise = AxisCommands(Commands.moveLengthwise, Commands.moveBackward, Commands.moveForward)
	val moveLateral = AxisCommands(Commands.moveLateral, Commands.moveLeft, Commands.moveRight)
}

fun getFlyingInput(bindings: Bindings, gamepad: Int): Int =
	when {
		isButtonPressed(bindings, gamepad, Commands.mobilityAction) -> 1
		isButtonPressed(bindings, gamepad, Commands.crouch) -> -1
		else -> 0
	}

fun newPlayerInput(bindings: Bindings, gamepad: Int, accessoryIsEngaged: Boolean): PlayerInput {
	return PlayerInput(
		mobilityAction = isButtonJustPressed(bindings, gamepad, Commands.mobilityAction),
		primaryAction = if (accessoryIsEngaged)
			isButtonPressed(bindings, gamepad, Commands.primaryAction)
		else
			isButtonJustPressed(bindings, gamepad, Commands.primaryAction),
		utilityAction = isButtonJustPressed(bindings, gamepad, Commands.utilityAction),
		lookX = getAxisState(bindings, gamepad, StandardAxisCommands.lookX),
		lookY = getAxisState(bindings, gamepad, StandardAxisCommands.lookY),
		moveLengthwise = getAxisState(bindings, gamepad, StandardAxisCommands.moveLengthwise),
		moveLateral = getAxisState(bindings, gamepad, StandardAxisCommands.moveLateral),
		interact = isButtonJustPressed(bindings, gamepad, Commands.interact),
		fly = getFlyingInput(bindings, gamepad),
		actionChange = when {
			isButtonJustPressed(bindings, gamepad, Commands.nextAction) -> ActionChange.next
			isButtonJustPressed(bindings, gamepad, Commands.previousAction) -> ActionChange.previous
			else -> ActionChange.noChange
		}
	)
}

fun newPlayerInput(state: InputState, player: Id, playerIndex: Int, accessoryIsEngaged: Boolean): PlayerInput {
	val bindings = getPlayerBindings(state, player, playerIndex)
	return if (bindings != null) {
		val gamepad = getPlayerGamepad(state, playerIndex)
		newPlayerInput(bindings, gamepad, accessoryIsEngaged)
	} else
		emptyPlayerInput
}

fun newPlayerInputs(
	menuStacks: MenuStacks,
	state: InputState,
	players: PlayerMap,
	engagedAccessories: Map<Id, Id>
): PlayerInputs =
	players.mapValues { (player, index) ->
		if (getPlayerInputContext(menuStacks, player) == InputContexts.game)
			newPlayerInput(state, player, index, engagedAccessories.containsKey(player))
		else
			emptyPlayerInput
	}
