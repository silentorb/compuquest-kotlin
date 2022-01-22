package compuquest.clienting

import compuquest.clienting.audio.AudioOptions
import compuquest.clienting.display.DisplayOptions
import compuquest.clienting.input.defaultInputProfiles
import compuquest.clienting.input.defaultPlayerInputProfiles
import silentorb.mythic.configuration.loadYamlFile
import silentorb.mythic.configuration.saveYamlFile
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.haft.InputProfileOptionsMap

data class UiOptions(
	val showHud: Boolean = true,
)

data class AppOptions(
	val audio: AudioOptions,
	val display: DisplayOptions,
	val ui: UiOptions,
)

data class SerializableInputOptions(
	val profiles: InputProfileOptionsMap = defaultInputProfiles(),
	val playerProfiles: List<Int> = defaultPlayerInputProfiles(),
)

data class SerializedAppOptions(
	val audio: AudioOptions = AudioOptions(),
	val display: DisplayOptions = DisplayOptions(),
	val ui: UiOptions = UiOptions(),
	val input: SerializableInputOptions = SerializableInputOptions(),
)

const val optionsFile = "options.yaml"

fun saveOptions(options: SerializedAppOptions) {
	saveYamlFile(optionsFile, options)
}

fun loadOptions(): SerializedAppOptions {
	val config = if (!getDebugBoolean("FORCE_DEFAULT_OPTIONS"))
		loadYamlFile<SerializedAppOptions>(optionsFile)
	else
		null

	if (config != null)
		return config

	val newConfig = SerializedAppOptions()
	saveOptions(newConfig)
	return newConfig
}

fun newSerializableAppOptions(client: Client): SerializedAppOptions =
	SerializedAppOptions(
		audio = client.options.audio,
		display = client.options.display,
		ui = client.options.ui,
		input = SerializableInputOptions(
			profiles = client.input.profileOptions,
			playerProfiles = client.input.playerProfiles,
		)
	)

fun checkSaveOptions(previous: Client, next: Client) {
	if (
//		previous.options != next.options ||
		previous.input.profileOptions != next.input.profileOptions ||
		previous.input.playerProfiles != next.input.playerProfiles
	) {
		saveOptions(newSerializableAppOptions(next))
	}
}

var saveOptionsCheckTimer: Float = 0f

fun checkSaveOptions(previous: Client, next: Client, delta: Float) {
	saveOptionsCheckTimer += delta
	if (saveOptionsCheckTimer > 1f) {
		checkSaveOptions(previous, next)
		saveOptionsCheckTimer = 0f
	}
}
