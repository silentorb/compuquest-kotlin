package compuquest.simulation.combat

import compuquest.simulation.general.AccessoryEffects
import compuquest.simulation.general.Deck
import compuquest.simulation.general.getAccessoriesSequence
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues
import kotlin.math.max

const val damageCommand = "damage"

fun applyDamage(deck: Deck, actor: Id, characterEvents: Events): Int {
  val damages = filterEventValues<Int>(damageCommand, characterEvents)
  return if (damages.any()) {
    val damageReduction = getAccessoriesSequence(deck.accessories, actor)
      .filter { it.value.effect == AccessoryEffects.damageReduction }
      .sumBy { it.value.strengthInt }

    damages.sumBy { -max(0, it - damageReduction) }
  } else
    0
}
