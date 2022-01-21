package compuquest.clienting.input

import compuquest.clienting.Client
import compuquest.clienting.gui.Screens
import compuquest.simulation.general.Deck
import compuquest.simulation.happening.TryActionEvent
import compuquest.simulation.happening.tryActionEvent
import compuquest.simulation.input.Commands
import compuquest.simulation.input.PlayerInput
import compuquest.simulation.input.PlayerInputs
import compuquest.simulation.input.emptyPlayerInput
import silentorb.mythic.ent.Id
import silentorb.mythic.haft.*
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

fun gatherPlayerUseActions(deck: Deck, playerInputs: PlayerInputs): Events =
	deck.players.keys
		.mapNotNull { actor ->
			if (playerInputs[actor]?.primaryAction == true) {
				val character = deck.characters[actor]
				if (character?.activeAccessory != null)
					newEvent(tryActionEvent, actor, TryActionEvent(action = character.activeAccessory))
				else
					null
			} else
				null
		}

fun getPlayerProfile(state: InputState, playerIndex: Int): InputProfile? =
	state.profiles[state.playerProfiles[playerIndex]]

val uiCommands = listOf(
	Commands.activate,
	Commands.navigate,
	Commands.menuBack,
	Commands.newGame,
	Commands.addPlayer,
)

fun getUiCommandEvents(bindings: Bindings, gamepad: Int, player: Id): Events =
	uiCommands.flatMap { command ->
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

fun getUiCommandEvents(state: InputState, player: Id, playerIndex: Int): Events {
	val bindings = getPlayerBindings(state, player, playerIndex)
	return if (bindings != null) {
		val gamepad = getPlayerGamepad(state, playerIndex)
		getUiCommandEvents(bindings, gamepad, player)
	} else
		listOf()
}

fun getUiCommandEvents(client: Client): Events =
	client.players.flatMap { (player, index) ->
		getUiCommandEvents(client.input, player, index)
	}

object StandardAxisCommands {
	val lookX = AxisCommands(Commands.lookX, Commands.lookDown, Commands.lookUp)
	val lookY = AxisCommands(Commands.lookY, Commands.lookLeft, Commands.lookRight)
	val moveLengthwise = AxisCommands(Commands.moveLengthwise, Commands.moveBackward, Commands.moveForward)
	val moveLateral = AxisCommands(Commands.moveLateral, Commands.moveLeft, Commands.moveRight)
}

fun newPlayerInput(bindings: Bindings, gamepad: Int): PlayerInput {
	return PlayerInput(
		jump = isButtonJustPressed(bindings, gamepad, Commands.jump),
		primaryAction = isButtonPressed(bindings, gamepad, Commands.primaryAction),
		lookX = getAxisState(bindings, gamepad, StandardAxisCommands.lookX),
		lookY = getAxisState(bindings, gamepad, StandardAxisCommands.lookY),
		moveLengthwise = getAxisState(bindings, gamepad, StandardAxisCommands.moveLengthwise),
		moveLateral = getAxisState(bindings, gamepad, StandardAxisCommands.moveLateral),
	)
}

fun newPlayerInput(state: InputState, player: Id, playerIndex: Int): PlayerInput {
	val bindings = getPlayerBindings(state, player, playerIndex)
	return if (bindings != null) {
		val gamepad = getPlayerGamepad(state, playerIndex)
		newPlayerInput(bindings, gamepad)
	} else
		emptyPlayerInput
}

fun newPlayerInputs(state: InputState, players: PlayerMap): PlayerInputs =
	players.mapValues { (player, index) ->
		newPlayerInput(state, player, index)
	}
