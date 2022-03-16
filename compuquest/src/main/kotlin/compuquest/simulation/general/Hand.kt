package compuquest.simulation.general

import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event

data class Hand(
	val id: Id? = null,
	val components: List<Any>,
)

typealias Hands = List<Hand>

const val newHandCommand = "newHand"

fun newHandEvent(hand: Hand) =
	Event(newHandCommand, value = hand)

fun newHandEvents(hands: Hands) =
	hands.map(::newHandEvent)

inline fun <reified T> getHandComponent(hand: Hand): T? =
	hand.components
		.filterIsInstance<T>()
		.firstOrNull()
