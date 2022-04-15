package silentorb.mythic.audio

import godot.core.Transform
import godot.core.Vector3
import silentorb.mythic.ent.Id
import java.nio.ShortBuffer

typealias SoundBuffer = Int

typealias SoundType = String

typealias SoundDurations = Map<SoundType, Float>

data class SoundData(
    val type: SoundType,
    val buffer: SoundBuffer,
    val duration: Float
)

typealias SoundMap = Map<Id, SoundBuffer>

data class Sound(
    val type: SoundType,
    val volume: Float,
    val progress: Float = 0f,
    val location: Vector3? = null
)

typealias SoundLibrary = Map<SoundType, SoundData>

data class AudioState(
    val sounds: SoundMap,
    val volume: Float
)

data class CalculatedSound(
    val remainingSamples: Int,
    val progress: Int,
    val instrument: (Int) -> Short,
    val gain: Float
)

data class AudioConfig(
    var soundVolume: Float = 0.75f
)
