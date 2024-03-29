package compuquest.simulation.general

import compuquest.simulation.definition.ResourceType

data class IntResource(
  val value: Int,
  val max: Int = value
)

typealias ResourceMap = Map<ResourceType, Int>

fun displayText(resource: IntResource): String =
  "${resource.value}/${resource.max}"

fun clampResource(value: Int, max: Int): Int {
  return when {
    value < 0 -> 0
    value > max -> max
    else -> value
  }
}

fun modifyResource(value: Int, max: Int, mod: Int): Int =
  clampResource(mod + value, max)


fun modifyResource(mod: Int, resource: IntResource): IntResource =
  resource.copy(
    value = modifyResource(resource.value, resource.max, mod)
  )

fun invertResourceMap(resourceMap: ResourceMap): ResourceMap =
  resourceMap.mapValues { (_, value) ->
    -value
  }
