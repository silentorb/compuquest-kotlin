package compuquest.simulation.general

import silentorb.mythic.ent.Id

const val maxPartySize = 4

data class Player(
  val faction: Id,
  val party: List<Id> = listOf(),
)
