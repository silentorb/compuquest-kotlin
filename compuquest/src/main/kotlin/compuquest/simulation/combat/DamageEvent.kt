package compuquest.simulation.combat

import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

data class DamageEvent(
  val damage: Damage,
  val position: Vector3? = null
)

fun newDamageEvents(target: Id, source: Id, damages: List<DamageDefinition>, position: Vector3? = null): Events =
  damages.map { damage ->
    Event(
      type = damageEvent,
      target = target,
      value = DamageEvent(
        position = position,
        damage = Damage(
          type = damage.type,
          amount = damage.amount,
          source = source
        )
      )
    )
  }
