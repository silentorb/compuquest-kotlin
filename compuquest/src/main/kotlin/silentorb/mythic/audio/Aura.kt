package silentorb.mythic.audio

import godot.core.Transform
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.platforming.PlatformAudio

fun newAudioState(volume: Float) =
	AudioState(
		sounds = mapOf(),
		volume = volume
	)

fun updateSoundPlaying(
	audio: PlatformAudio,
	previousSounds: Table<Sound>,
	newSounds: Table<Sound>,
	library: SoundLibrary,
	listenerTransforms: List<Transform>,
	gain: Float
): (SoundMap) -> SoundMap = { soundMap ->
	if (listenerTransforms.size < 2) {
		audio.update(gain, listenerTransforms.firstOrNull()?.origin)
	} else {
//		val soundLocations = soundMap
//			.mapNotNull { (id, source) ->
//				val actualLocation = previousSounds[id]?.location
//				if (actualLocation != null) {
//					val nearestListener = listenerTransforms.minByOrNull { it.origin.distanceTo(actualLocation) }!!
//					Transform().translated(actualLocation) * nearestListener
//				}
//				else
//					null
//			}
//		audio.update(gain) { source ->
//			val id = soundMap.firstNotNullOf { it.value == source }
//			if (id != null) {
//
//			}
//			else
//				null
//		}
	}
	val newSoundMappings = newSounds
		.mapNotNull { (id, sound) ->
			val definition = library[sound.type]!!
			val position = sound.location ?: Vector3.ZERO
			val source = audio.play(definition.buffer, sound.volume, position)
			if (source == null)
				null
			else
				id to source
		}
		.associate { it }

	val playingSounds = audio.getPlayingSounds()
	soundMap
		.filterValues { playingSounds.contains(it) }
		.plus(newSoundMappings)
}

fun updateSound(delta: Float): (Sound) -> Sound = { sound ->
	sound.copy(
		progress = sound.progress + delta
	)
}

fun finishedSounds(soundDurations: SoundDurations): (Table<Sound>) -> Set<Id> = { sounds ->
	sounds
		.filterValues { it.progress >= soundDurations[it.type] ?: 0f }
		.keys
}
