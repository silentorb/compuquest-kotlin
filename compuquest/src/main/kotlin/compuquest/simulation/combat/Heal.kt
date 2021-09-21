package compuquest.simulation.combat

import compuquest.simulation.general.*
import godot.Spatial
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

fun heal(action: Id, accessory: Accessory, target: Id): Events {

  return listOf(
    Event(useActionCommand, action),
    modifyHealth(target, accessory.strengthInt),
  )
}
