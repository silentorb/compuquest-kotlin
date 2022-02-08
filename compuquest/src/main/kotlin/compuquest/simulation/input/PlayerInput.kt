package compuquest.simulation.input

import compuquest.simulation.general.*
import compuquest.simulation.happening.TryActionEvent
import compuquest.simulation.happening.tryActionEvent
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.newEvent

enum class ActionChange {
	noChange,
	previous,
	next,
}

data class PlayerInput(
	val jump: Boolean,
	val lookX: Float,
	val lookY: Float,
	val moveLengthwise: Float,
	val moveLateral: Float,
	val primaryAction: Boolean,
	val interact: Boolean,
	val actionChange: ActionChange,
)

typealias PlayerInputs = Map<Id, PlayerInput>

val emptyPlayerInput = PlayerInput(
	jump = false,
	lookX = 0f,
	lookY = 0f,
	moveLengthwise = 0f,
	moveLateral = 0f,
	primaryAction = false,
	interact = false,
	actionChange = ActionChange.noChange,
)

fun getPlayerPrimaryActionEvents(deck: Deck, actor: Id): Events {
	val character = deck.characters[actor]
	return if (character != null && character.activeAccessory != emptyId)
		listOf(newEvent(tryActionEvent, actor, TryActionEvent(action = character.activeAccessory)))
	else
		listOf()
}

fun gatherPlayerUseActions(deck: Deck, playerInputs: PlayerInputs): Events =
	deck.players
		.flatMap { (actor, player) ->
			val input = playerInputs[actor]
			if (input != null) {
				when {
					input.interact -> getPlayerInteractionEvents(deck, actor, player)
					input.primaryAction -> getPlayerPrimaryActionEvents(deck, actor)
					else -> listOf()
				}
			} else
				listOf()
		}
