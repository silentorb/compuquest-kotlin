package compuquest.clienting.display

import godot.OS
import silentorb.mythic.godoting.tempCatchStatement
import silentorb.mythic.spatial.Vector2i

enum class WindowMode {
  windowed,
  fullscreen,
  borderlessFullscreen
}

data class DisplayOptions(
  val fullscreenResolution: Vector2i = Vector2i(1920, 1080),
  val windowedResolution: Vector2i? = null,
  val windowMode: WindowMode = WindowMode.windowed,
  val vsync: Boolean = true,
  val fov: Float = 80f,
)


fun applyDisplayOptions(options: DisplayOptions) {
  tempCatchStatement {
    OS.windowBorderless = options.windowMode == WindowMode.borderlessFullscreen
    if (options.windowMode == WindowMode.windowed && options.windowedResolution == null) {
      OS.windowMaximized = true
    }
  }
}
