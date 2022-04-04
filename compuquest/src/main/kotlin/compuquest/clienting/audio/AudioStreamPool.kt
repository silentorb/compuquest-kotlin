package compuquest.clienting.audio

import godot.Node
import godot.core.Vector3
import godot.global.GD
import scripts.entities.AudioPlayer
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues
import silentorb.mythic.happening.newEvent

data class AudioPlayerPool(
	val maxSounds: Int = 64,
	val players: MutableList<AudioPlayer> = arrayListOf(),
)

const val playSoundEvent = "playSound3d"

data class SpatialSound(
	val name: String,
	val location: Vector3,
	val gain: Float = 1f,
	val parent: Node? = null,
)

fun playSound(sound: SpatialSound): Event =
	newEvent(playSoundEvent, value = sound)

fun updateAudio(root: Node, pool: AudioPlayerPool, events: Events) {
	val newSounds = filterEventValues<SpatialSound>(playSoundEvent, events)

	var checkedIndex = 0
	val poolSize = pool.players.size

	for (sound in newSounds) {
		for (i in checkedIndex until poolSize) {
			++checkedIndex
			if (!pool.players[i].playing) {
				break
			}
		}

		val player = if (checkedIndex >= poolSize) {
			if (pool.players.size >= pool.maxSounds)
				break

			val newPlayer = AudioPlayer()
			pool.players.add(newPlayer)
			newPlayer
		} else
			pool.players[checkedIndex]

		if (sound.parent != null || player.getParent() == null) {
			val parent = sound.parent ?: root
			parent.addChild(player)
		}
		player.translation = sound.location
		player.stream = GD.load("res://assets/audio/prototype/${sound.name}.wav")
		player.play()
	}
}
