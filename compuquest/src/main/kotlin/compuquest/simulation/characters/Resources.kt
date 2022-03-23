package compuquest.simulation.characters

import compuquest.simulation.general.clampResource
import silentorb.mythic.happening.GenericEvent

typealias ResourceTypeName = String

typealias ResourceMap = Map<ResourceTypeName, Int>

val emptyResourceMap: ResourceMap = mapOf()

enum class ResourceOperation {
	add,
	set,
}

const val modifyResourceEvent = "modifyResource"

data class ModifyResource(
	val resource: String,
	val amount: Int,
	val operation: ResourceOperation = ResourceOperation.add,
)

typealias ModifyResourceEvents = List<GenericEvent<ModifyResource>>

data class ResourceContainer(
	val value: Int,
	val max: Int = value
)

data class ResourceBundle(
	val values: ResourceMap,
	val maximums: ResourceMap = emptyResourceMap
)

fun modifyResourceWithEvents(
	mods: List<ModifyResource>,
	resource: String,
	previous: Int,
	max: Int,
	mod: Int = 0
): Int {
	val (replacements, additions) = mods
		.filter { it.resource == resource }
		.partition { it.operation == ResourceOperation.set }

	val replacement = replacements.maxOfOrNull { it.amount }
	val raw = replacement ?: previous + additions.sumOf { it.amount } + mod

	return clampResource(raw, max)
}
