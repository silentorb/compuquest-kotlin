package silentorb.mythic.audio

import godot.core.Vector3
import kotlin.math.sin

private var kz = 0L

fun sineTest(listenerPosition: Vector3?) = listOf(
    CalculatedSound(
        remainingSamples = 1000000,
        progress = kz.toInt(),
        instrument = { (sin((kz + it).toDouble() * 0.1) * Short.MAX_VALUE).toInt().toShort() },
        gain = distanceAttenuation(listenerPosition, Vector3.ZERO)
    )
)

fun updateSineTest(samples: Int) {
  kz += samples
}
