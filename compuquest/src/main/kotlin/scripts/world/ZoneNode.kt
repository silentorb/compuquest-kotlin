package scripts.world

import godot.Engine
import godot.Node
import godot.PackedScene
import godot.annotation.*
import godot.global.GD

const val zoneGroup = "zone"

@Tool
@RegisterClass
class ZoneNode : Node() {

  @Export
  @RegisterProperty
  var scene: PackedScene? = null
    set(value) {
      if (field != value) {
        field = value
        onSceneChanged()
      }
    }

  @Export
  @RegisterProperty
  var active: Boolean = true
    set(value) {
      GD.print("foo")
      if (field != value) {
        field = value
        onActiveChanged()
      }
    }

  val isActive: Boolean
    get() = !Engine.editorHint || active

  fun clear() {
    getChildren()
      .filterIsInstance<Node>()
      .forEach { it.queueFree() }
  }

  fun onSceneChanged() {
    GD.print("onSceneChanged")
    if (isActive) {
      clear()
      val localScene = scene
      if (localScene != null) {
        GD.print("localScene != null")
        val child = localScene.instance()
        if (child != null) {
          GD.print("Adding zone scene instance")
          addChild(child)
        }
      }
    }
  }

  fun onActiveChanged() {
    if (isActive)
      onSceneChanged()
    else
      clear()
  }
}
