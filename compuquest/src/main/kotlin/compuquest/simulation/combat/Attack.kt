package compuquest.simulation.combat

import compuquest.simulation.general.Accessory
import silentorb.mythic.ent.Id

const val attackCommand = "attack"

data class Attack(
  val actor: Id,
  val accessory: Accessory,
)
