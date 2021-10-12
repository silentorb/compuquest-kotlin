package compuquest.clienting

import compuquest.clienting.audio.AudioOptions
import compuquest.clienting.display.DisplayOptions
import compuquest.clienting.input.InputOptions
import silentorb.mythic.configuration.loadYamlFile
import silentorb.mythic.configuration.saveYamlFile

data class UiOptions(
  val showHud: Boolean = true,
)

data class AppOptions(
  val audio: AudioOptions = AudioOptions(),
  val display: DisplayOptions = DisplayOptions(),
  val input: InputOptions = InputOptions(),
  val ui: UiOptions = UiOptions(),
)

const val optionsFile = "options.yaml"

fun saveOptions(options: AppOptions) {
  saveYamlFile(optionsFile, options)
}

fun loadOptions(): AppOptions {
  val config = loadYamlFile<AppOptions>(optionsFile)
  if (config != null)
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
