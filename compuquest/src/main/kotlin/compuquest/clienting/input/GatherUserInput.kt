package compuquest.clienting.input

import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import compuquest.simulation.general.getPlayer
import compuquest.simulation.happening.TryActionEvent
import compuquest.simulation.happening.tryActionEvent
import compuquest.simulation.input.Commands
import compuquest.simulation.input.PlayerInput
import godot.Input
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId
import silentorb.mythic.ent.mappedCache
import silentorb.mythic.haft.Bindings
import silentorb.mythic.haft.BindingsMap
import silentorb.mythic.haft.getAxisState
import silentorb.mythic.haft.isButtonPressed
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
	keyStrokes.filter { Input.isActionJustReleased(it) }
		.map { newEvent(it, player) } +
			actionPresses.filter { Input.isActionPressed(it) }
				.mapNotNull {
					val character = deck.characters[player]
					if (character?.activeAccessory != null)
						newEvent(tryActionEvent, player, TryActionEvent(action = character.activeAccessory))
					else
						null
				}

fun gatherDefaultPlayerInput(world: World): Events {
	val player = getPlayer(world)?.key
	return if (player != null)
		gatherUserInput(world.deck, player)
	else
		listOf()
}

fun getPlayerProfile(options: InputOptions, player: Id): InputProfile? =
	options.profiles[options.playerProfiles[player]]

fun newPlayerInput(bindings: Bindings): PlayerInput {
	return PlayerInput(
		jump = isButtonPressed(bindings, Commands.jump),
		primaryAction = isButtonPressed(bindings, Commands.primaryAction),
		lookHorizontal = getAxisState(bindings, Commands.lookHorizontal, Commands.lookDown, Commands.lookUp),
		lookVertical = getAxisState(bindings, Commands.lookVertical, Commands.lookLeft, Commands.lookRight),
		moveHorizontal = getAxisState(bindings, Commands.moveHorizontal, Commands.moveDown, Commands.moveUp),
		moveVertical = getAxisState(bindings, Commands.moveVertical, Commands.moveLeft, Commands.moveRight),
	)
}
