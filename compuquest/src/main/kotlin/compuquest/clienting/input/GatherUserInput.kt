package compuquest.clienting.input

import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import compuquest.simulation.general.getPlayer
import compuquest.simulation.happening.TryActionEvent
import compuquest.simulation.happening.tryActionEvent
import compuquest.simulation.input.Commands
import compuquest.simulation.input.PlayerInput
import compuquest.simulation.input.PlayerInputs
import godot.Input
import silentorb.mythic.ent.Id
import silentorb.mythic.haft.*
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

val keyStrokes = setOf(
	Commands.interact,
//  Commands.manageMembers,
//  Commands.manageQuests,
	Commands.menuBack,
	Commands.newGame,
)

val actionPresses = setOf(
	Commands.primaryAction,
//  Commands.secondaryAction,
)

fun gatherUserInput(deck: Deck, player: Id): Events =
//	keyStrokes.filter { Input.isActionJustReleased(it) }
//		.map { newEvent(it, player) } +
	actionPresses.filter { Input.isActionPressed(it) }
		.mapNotNull {
			val character = deck.characters[player]
			if (character?.activeAccessory != null)
				newEvent(tryActionEvent, player, TryActionEvent(action = character.activeAccessory))
			else
				null
		}

fun gatherDefaultPlayerInputOld(world: World): Events {
	val player = getPlayer(world)?.key
	return if (player != null)
		gatherUserInput(world.deck, player)
	else
		listOf()
}

fun getPlayerProfile(state: InputState, player: Id): InputProfile? =
	state.profiles[state.playerProfiles[player]]

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

fun newPlayerInput(state: InputState, player: Id): PlayerInput? {
	val profile = getPlayerProfile(state, player)
	return if (profile != null) {
		val gamepad = getPlayerGamepad(state, player)
		newPlayerInput(profile.bindings, gamepad)
	} else
		null
}

fun newPlayerInputs(state: InputState, players: Collection<Id>): PlayerInputs =
	players.mapNotNull {
		val input = newPlayerInput(state, it)
		if (input != null)
			it to input
		else
			null
	}
		.associate { it }
