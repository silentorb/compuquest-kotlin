package compuquest.clienting.audio

import compuquest.simulation.general.Hand
import compuquest.simulation.general.newHandEvent
import godot.AudioStream
import godot.Node
import godot.core.Vector3
import godot.global.GD
import scripts.entities.AudioPlayer
import silentorb.mythic.audio.Sound
import silentorb.mythic.ent.nullableMappedCache
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues

data class AudioPlayerPool(
	val maxSounds: Int = 64,
	val players: MutableList<AudioPlayer> = arrayListOf(),
)

typealias SoundCache = MutableMap<String, AudioStream>

const val playSoundEvent = "playSound3d"

typealias SpatialSound = Sound
//data class SpatialSound(
//	val name: String,
//	val location: Vector3,
//	val gain: Float = 1f,
//	val parent: Node? = null,
//)

data class SoundDefinition(
	val name: String,
	val volume: Float = 1f,
)

fun playSound(sound: SpatialSound): Event =
	newHandEvent(Hand(components = listOf(sound)))
//	newEvent(playSoundEvent, value = sound)

fun playSound(name: String, location: Vector3, volume: Float = 1f): Event =
	playSound(SpatialSound(name, volume, location = location))

fun playSound(sound: SoundDefinition, location: Vector3): Event =
	playSound(SpatialSound(sound.name, sound.volume, location = location))

fun updateAudio(soundCache: SoundCache, root: Node, pool: AudioPlayerPool, events: Events) {
	val newSounds = filterEventValues<SpatialSound>(playSoundEvent, events)

	var checkedIndex = 0
	val poolSize = pool.players.size
	if (poolSize > 0) {
		val j = pool.players[0].playing
		val k = 0
	}

//	for (sound in newSounds) {
//		val stream = nullableMappedCache(soundCache, 0, sound.name) {
//			GD.load("res://assets/audio/prototype/${it}.wav")
//		} ?: continue
//
//		for (i in checkedIndex until poolSize) {
//			if (!pool.players[i].playing) {
//				break
//			}
//			++checkedIndex
//		}
//
//		val player = if (checkedIndex >= poolSize) {
//			if (pool.players.size >= pool.maxSounds)
//				break
//
//			val newPlayer = AudioPlayer()
////			newPlayer.unitSize = 1.0
//			newPlayer.attenuationModel = 1L
//			pool.players.add(newPlayer)
//			println("New Sound Player")
//			newPlayer
//		} else
//			pool.players[checkedIndex]
//
//		println(checkedIndex)
//		if (sound.parent != null || player.getParent() == null) {
//			val parent = sound.parent ?: root
//			parent.addChild(player)
//		}
//		player.translation = sound.location
//		player.unitDb = -100.0
//		player.unitSize = 50.0
//		player.attenuationModel = 1L
//		player.stream = stream
//		player.play()
//	}
}
