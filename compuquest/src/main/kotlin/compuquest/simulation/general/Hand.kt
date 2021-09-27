package compuquest.simulation.general

import silentorb.mythic.happening.Event
import silentorb.mythic.ent.Id

data class Hand(
  val id: Id? = null,
  val components: List<Any>,
)

typealias Hands = List<Hand>

const val newHandCommandKey = "newHand"

fun newHandEvent(hand: Hand) =
  Event(newHandCommandKey, value = hand)
