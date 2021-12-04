package compuquest.simulation.characters

import compuquest.simulation.general.clampResource
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

typealias ResourceTypeName = String

typealias ResourceMap = Map<ResourceTypeName, Int>

val emptyResourceMap: ResourceMap = mapOf()

enum class ResourceOperation {
  add,
  replace,
}

data class ModifyResource(
    val actor: Id,
    val resource: String,
    val amount: Int,
    val operation: ResourceOperation = ResourceOperation.add,
)

data class ResourceContainer(
    val value: Int,
    val max: Int = value
)

data class ResourceBundle(
    val values: ResourceMap,
    val maximums: ResourceMap = emptyResourceMap
)

fun modifyResourceWithEvents(events: Events, actor: Id, resource: String, previous: Int, max: Int, mod: Int): Int {
  val modifyEvents =
      events
          .filterIsInstance<ModifyResource>()

  val (replacements, additions) = modifyEvents
      .filter { it.actor == actor && it.resource == resource }
      .partition { it.operation == ResourceOperation.replace }

  val replacement = replacements.maxOfOrNull { it.amount }
  val base = replacement ?: previous
  val raw = base + additions.sumOf { it.amount } + mod

  return clampResource(raw, max)
}
