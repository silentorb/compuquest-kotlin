package compuquest.simulation.combat

import silentorb.mythic.ent.Id

data class Missile(
  val damage: Int,
  val target: Id,
  val owner: Id,
)

