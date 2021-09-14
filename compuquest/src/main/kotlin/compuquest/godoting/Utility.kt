package compuquest.godoting

import godot.PackedScene
import godot.global.GD

inline fun <reified T>instantiateScene(path: String): T? {
  val scene = GD.load<PackedScene>(path)
  return scene?.instance() as? T
}

inline fun <reified T>entityFromScene(name: String): T? =
  instantiateScene("res://entities/$name.tscn")
