package compuquest.simulation.happening

import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.emptyId

data class TryActionEvent(
  val action: Id,
  val targetEntity: Id = emptyId,
  val targetLocation: Vector3? = null
)
