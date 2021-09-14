package compuquest.simulation.general

import compuquest.simulation.happening.Event
import silentorb.mythic.ent.Id

data class Hand(
  val id: Id? = null,
  val components: List<Any>,
)

const val newHandCommandKey = "newHand"

fun newHandCommand(hand: Hand) =
  Event(newHandCommandKey, value = hand)
