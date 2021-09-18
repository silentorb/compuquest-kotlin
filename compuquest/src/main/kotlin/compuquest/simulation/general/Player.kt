package compuquest.simulation.general

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key

const val maxPartySize = 4
const val playerFaction = "player"

data class Player(
  val faction: Key,
  val party: List<Id> = listOf(),
)
