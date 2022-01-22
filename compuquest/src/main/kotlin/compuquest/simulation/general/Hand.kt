package compuquest.simulation.general

import silentorb.mythic.happening.Event
import silentorb.mythic.ent.Id

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
