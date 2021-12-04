package compuquest.simulation.characters

import compuquest.simulation.combat.Destructible
import compuquest.simulation.general.HighInt
import compuquest.simulation.general.Int1000
import compuquest.simulation.general.highIntScale
import compuquest.simulation.general.intMinute
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

data class ResourceRates(
  val distanceTraveledDrainDuration: Int, // With no other factors, moving X feet will deplete full nourishment
  val timeDrainDuration: Int, // With no other factors, waiting X frames will deplete full nourishment
)
val standardEnergyRates = ResourceRates(
    distanceTraveledDrainDuration = 1000,
    timeDrainDuration = intMinute * 30,
)

const val healthTimeDrainDuration = intMinute * 30

fun getResourceTimeCost(timeDrainDuration: Int, frames: Int): HighInt {
  val delta = highIntScale * frames * 100
  return delta / timeDrainDuration
}

fun getMovementCost(rates: ResourceRates, frames: Int = 1, velocity: Int1000): Int {
  val distanceTraveledDrainDuration = rates.distanceTraveledDrainDuration
  return if (distanceTraveledDrainDuration == 0)
    0
  else {
    val movementCostPerFoot = highIntScale / distanceTraveledDrainDuration
    movementCostPerFoot * frames * velocity / 1000 / 60
  }
}

fun getEnergyExpense(rates: ResourceRates, frames: Int = 1, velocity: Int1000): HighInt {
  val movementCost = getMovementCost(rates, frames, velocity)
  val timeCost = getResourceTimeCost(rates.timeDrainDuration, frames)
  return movementCost + timeCost
}

fun getRoundedAccumulation(expense: HighInt): Int =
    expense / highIntScale

//fun updateEnergy(events: Events, actor: Id, character: Character, destructible: Destructible, mod: Int = 0) =
//    modifyResourceWithEvents(events, actor, ResourceTypes.energy, character.energy, destructible.health, mod)
