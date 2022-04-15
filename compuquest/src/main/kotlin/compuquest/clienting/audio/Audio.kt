package compuquest.clienting.audio

import compuquest.clienting.Client
import compuquest.definition.Sounds
import compuquest.simulation.combat.getToolTransform
import compuquest.simulation.general.World
import godot.core.Transform
import silentorb.mythic.audio.*
import silentorb.mythic.ent.*
import silentorb.mythic.platforming.PlatformAudio

const val maxSoundDistance: Float = 30f

fun updateAudioStateSounds(
	client: Client,
	previousSounds: Table<Sound>,
	nextSounds: Table<Sound>,
	listenerTransforms: List<Transform>
): (AudioState) -> AudioState = { state ->
	val audio = client.platformAudio
	if (audio.isActive) {
		val newSounds = nextSounds.filterKeys { !previousSounds.keys.contains(it) }
			.filterValues { sound ->
				sound.location == null || listenerTransforms.none() ||
						listenerTransforms.minOf { it.origin.distanceTo(sound.location) < maxSoundDistance }
			}

		val sounds =
			updateSoundPlaying(audio, previousSounds, newSounds, client.soundLibrary, listenerTransforms, state.volume)(state.sounds)

		state.copy(
			sounds = sounds
		)
	} else
		state
}

fun loadAudioResource(audio: PlatformAudio, name: String) =
	audio.loadSound("assets/audio/prototype/$name.ogg")
//audio.loadSound(getResourceUrl("audio/$name.ogg")!!.file.drop(1))

fun loadSounds(audio: PlatformAudio): SoundLibrary =
	if (audio.isActive)
		reflectProperties<String>(Sounds).mapIndexed { i, entry ->
			val (buffer, duration) = loadAudioResource(audio, entry)
			val sound = SoundData(
				type = entry,
				buffer = buffer,
				duration = duration
			)
			Pair(sound.type, sound)
		}
			.associate { it }
	else
		mapOf()

fun updateClientAudio(client: Client, worlds: List<World>, audioState: AudioState): AudioState =
	if (worlds.size < 2)
		audioState
	else {
		val world = worlds.last()
		val deck = world.deck
		val listenerTransforms = deck.players.keys.mapNotNull { actor -> getToolTransform(deck, actor) }
		updateAudioStateSounds(
			client,
			worlds.first().deck.sounds,
			deck.sounds,
			listenerTransforms
		)(audioState)
	}
