package compuquest.clienting

import compuquest.clienting.audio.AudioOptions
import compuquest.clienting.display.DisplayOptions
import silentorb.mythic.configuration.loadYamlFile
import silentorb.mythic.configuration.saveYamlFile
import silentorb.mythic.debugging.getDebugBoolean

data class UiOptions(
  val showHud: Boolean = true,
)

data class AppOptions(
  val audio: AudioOptions = AudioOptions(),
  val display: DisplayOptions = DisplayOptions(),
  val ui: UiOptions = UiOptions(),
)

const val optionsFile = "options.yaml"

fun saveOptions(options: AppOptions) {
  saveYamlFile(optionsFile, options)
}

fun loadOptions(): AppOptions {
  val config = loadYamlFile<AppOptions>(optionsFile)
  if (config != null && !getDebugBoolean("FORCE_DEFAULT_OPTIONS"))
    return config

  val newConfig = AppOptions()
  saveOptions(newConfig)
  return newConfig
}

fun checkSaveOptions(previous: AppOptions, next: AppOptions) {
  if (previous != next) {
    saveOptions(next)
  }
}
