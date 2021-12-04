package silentorb.mythic.spatial

fun minMax(min: Int, max: Int, value: Int) =
  when {
    value < min -> min
    value > max -> max
    else -> value
  }

fun minMax(value: Float, min: Float, max: Float): Float =
  when {
    value < min -> min
    value > max -> max
    else -> value
  }
