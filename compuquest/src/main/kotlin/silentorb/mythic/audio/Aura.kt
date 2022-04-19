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

// If there is more than one listener, a fixed listener position is used
// and each sound is translated so that the nearest listener for each sound is it's anchor position
fun updateSoundPlaying(
	audio: PlatformAudio,
	previousSounds: Table<Sound>,
	newSounds: Table<Sound>,
	library: SoundLibrary,
	listenerTransforms: List<Transform>,
	gain: Float
): (SoundMap) -> SoundMap = { soundMap ->
//	if (listenerTransforms.size < 2) {
	val listenerLocation = if (listenerTransforms.size > 1)
		Vector3.ZERO
	else
		listenerTransforms.firstOrNull()?.origin

	audio.update(gain, listenerLocation)
//	} else {
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
//	}

	val newSoundMappings = newSounds
		.mapNotNull { (id, sound) ->
			val definition = library[sound.type]!!
			val actualLocation = sound.location
			val position = if (listenerTransforms.size > 1 && actualLocation != null) {
				val nearestListener = listenerTransforms.minByOrNull { it.origin.distanceTo(actualLocation) }!!
				(Transform().translated(actualLocation) * nearestListener).origin
			} else
				actualLocation ?: Vector3.ZERO

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
