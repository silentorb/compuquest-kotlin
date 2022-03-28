package compuquest.simulation.input

import compuquest.simulation.characters.AccessorySlot
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
	val lookX: Float,
	val lookY: Float,
	val moveLengthwise: Float,
	val moveLateral: Float,
	val primaryAction: Boolean,
	val utilityAction: Boolean,
	val mobilityAction: Boolean,
	val interact: Boolean,
	val actionChange: ActionChange,
	val fly: Int, // 1 for up, -1 for down, 0 for no change
)

typealias PlayerInputs = Map<Id, PlayerInput>

val emptyPlayerInput = PlayerInput(
	lookX = 0f,
	lookY = 0f,
	moveLengthwise = 0f,
	moveLateral = 0f,
	primaryAction = false,
	utilityAction = false,
	mobilityAction = false,
	interact = false,
	actionChange = ActionChange.noChange,
	fly = 0,
)

fun getPlayerActionEvents(deck: Deck, actor: Id, slot: AccessorySlot): Events {
	val character = deck.characters[actor]
	val accessory = character?.activeAccessories?.getOrDefault(slot, emptyId) ?: emptyId
	return if (accessory != emptyId)
		listOf(newEvent(tryActionEvent, actor, TryActionEvent(action = accessory)))
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
					input.primaryAction -> getPlayerActionEvents(deck, actor, AccessorySlot.primary)
					input.utilityAction -> getPlayerActionEvents(deck, actor, AccessorySlot.utility)
					else -> listOf()
				}
			} else
				listOf()
		}
