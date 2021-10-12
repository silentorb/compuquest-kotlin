package compuquest.clienting.display

import silentorb.mythic.spatial.Vector2i

enum class WindowMode {
  windowed,
  fullscreen,
  windowedFullscreen
}

data class DisplayOptions(
  val fullscreenResolution: Vector2i = Vector2i(1920, 1080),
  val windowedResolution: Vector2i? = null,
  val fullscreen: Boolean = false,
  val windowMode: WindowMode = WindowMode.windowed,
  val vsync: Boolean = true,
)
