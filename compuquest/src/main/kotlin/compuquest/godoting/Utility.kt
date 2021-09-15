package compuquest.godoting

import godot.Node
import godot.PackedScene
import godot.global.GD

inline fun <reified T> instantiateScene(path: String): T? {
  val scene = GD.load<PackedScene>(path)
  return scene?.instance() as? T
}

inline fun <reified T> entityFromScene(name: String): T? =
  instantiateScene("res://entities/$name.tscn")

// Godot-JVM raises warnings and sometimes becomes unstable around JNI calls that are
// not wrapped in exception handling.
// Hopefully this is a temporary solution and Godot-JVM will no longer need these
fun <T> tempCatch(action: () -> T): T? {
  return try {
    action()
  } catch (error: Throwable) {
    null
  }
}

fun deleteNode(node: Node) {
  tempCatch {
    node.queueFree()
  }
}
