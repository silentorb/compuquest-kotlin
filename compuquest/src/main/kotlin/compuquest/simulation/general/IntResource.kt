package compuquest.simulation.general

data class IntResource(
  val value: Int,
  val max: Int = value
)

typealias ResourceMap = Map<Key, IntResource>

fun clampResource(value: Int, max: Int): Int {
  return when {
    value < 0 -> 0
    value > max -> max
    else -> value
  }
}

fun modifyResource(value: Int, max: Int, mod: Int): Int =
  clampResource(mod + value, max)


fun modifyResource(resource: IntResource, mod: Int): Int =
  modifyResource(resource.value, resource.max, mod)
