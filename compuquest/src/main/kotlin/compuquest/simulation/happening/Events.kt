package compuquest.simulation.happening

import godot.core.Vector3
import silentorb.mythic.ent.Id

data class TryActionEvent(
  val action: Id,
  val targetEntity: Id? = null,
  val targetLocation: Vector3? = null
)
