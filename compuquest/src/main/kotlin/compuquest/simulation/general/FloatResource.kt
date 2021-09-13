package compuquest.simulation.general

data class FloatResource(
  val value: Float,
  val max: Float = value
)

fun clampResource(value: Float, max: Float): Float {
  return when {
    value < 0f -> 0f
    value > max -> max
    else -> value
  }
}

fun modifyResource(value: Float, max: Float, mod: Float): Float =
  clampResource(mod + value, max)


fun modifyResource(resource: FloatResource, mod: Float): Float =
  modifyResource(resource.value, resource.max, mod)
