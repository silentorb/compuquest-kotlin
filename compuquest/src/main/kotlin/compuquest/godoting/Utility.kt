package compuquest.godoting

import godot.Node
import godot.Object
import godot.PackedScene
import godot.core.VariantArray
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

inline fun <reified T>getVariantArray(property: String, value: Object?): List<T> =
  (value?.get(property) as? VariantArray<*>)?.filterIsInstance<T>() ?: listOf()

fun findChildren(node: Node, predicate: (Node) -> Boolean): List<Node> =
  node
    .getChildren()
    .filterIsInstance<Node>()
    .flatMap { child ->
      val selfList = if (predicate(child))
        listOf(child)
      else
        listOf()

      selfList + findChildren(child, predicate)
    }
