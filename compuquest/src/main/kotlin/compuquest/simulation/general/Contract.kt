package compuquest.simulation.general

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key

data class ContractDefinition(
  val type: Key,
  val reward: ResourceMap,
  val recipient: Id? = null, // For delivery quests
)

data class Contract(
  val client: Id,
  val contractor: Id,
  val definition: ContractDefinition,
)

object QuestTypes {
  val delivery = "delivery"
}
