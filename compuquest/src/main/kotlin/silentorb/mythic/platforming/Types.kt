package silentorb.mythic.platforming

import godot.core.Vector3
import java.nio.ShortBuffer

data class LoadSoundResult(
	val buffer: Int,
	val duration: Float
)

typealias AudioSourceLocationGetter = (Int) -> Vector3

interface PlatformAudio {
	val isActive: Boolean
	fun start(latency: Int)
	fun play(buffer: Int, volume: Float, location: Vector3?): Int?
	fun getPlayingSounds(): Collection<Int>
	fun update(gain: Float, listenerLocation: Vector3?)
	fun update(gain: Float, getSourceLocation: AudioSourceLocationGetter)
	fun loadSound(filename: String): LoadSoundResult
	fun loadSound(buffer: ShortBuffer, channels: Int, sampleRate: Int): LoadSoundResult
	fun unloadAllSounds()
	fun unloadSound(buffer: Int)
	fun stop()
}
