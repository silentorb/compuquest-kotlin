package silentorb.mythic.godoting

import godot.Node
import godot.Object
import godot.PackedScene
import godot.Resource
import godot.core.VariantArray
import godot.global.GD

inline fun <reified T> instantiateScene(path: String): T? {
  val scene = GD.load<PackedScene>(path)
  return scene?.instance() as? T
}

// Godot-JVM raises warnings and sometimes becomes unstable around JNI calls that are
// not wrapped in exception handling.
// Hopefully this is a temporary solution and Godot-JVM will no longer need these
fun <T> tempCatch(action: () -> T): T {
  return try {
    action()
  } catch (error: Throwable) {
    throw Error("An Error was thrown")
  }
}

fun tempCatchStatement(action: () -> Unit): Unit {
  return try {
    action()
  } catch (error: Throwable) {
    throw Error("An Error was thrown")
  }
}

fun deleteNode(node: Node) {
  tempCatch {
    node.queueFree()
  }
}

inline fun <reified T> getVariantArray(property: String, value: Object?): List<T> =
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

fun clearChildren(node: Node) {
  for (child in node.getChildren()) {
    node.removeChild(child as Node)
    child.queueFree()
  }
}

fun getFloat(value: Object, property: String): Float =
  (value.get(property) as? Double?)?.toFloat() ?: 0f

fun getFloatOrNull(value: Object, property: String): Float? =
  (value.get(property) as? Double?)?.toFloat()

fun getString(value: Object, property: String): String =
  value.get(property) as? String ?: ""

fun getNonEmptyString(value: Object, property: String): String? {
  return (value.get(property) as? String)?.ifEmpty { null }
}

fun getIntOrNull(value: Object, property: String): Int? =
  (value.get(property) as? Long?)?.toInt()

fun getInt(value: Object, property: String): Int =
  getIntOrNull(value, property) ?: 0

fun getBoolean(value: Object, property: String): Boolean =
  (value.get(property) as? Boolean?) ?: false

inline fun <reified T> getList(value: Object, property: String): List<T> =
  (value.get(property) as? VariantArray<*>)?.filterIsInstance<T>() ?: listOf()

val baseNamePattern = Regex("([\\w \\-]+)\\.\\w+$")
fun getResourceName(resource: Resource): String? =
  baseNamePattern.find(resource.resourcePath)?.groupValues?.drop(1)?.firstOrNull()

fun getScriptName(node: Node): String? {
  val resource = (node.getScript() as? Resource)
  return if (resource != null)
    getResourceName(resource)
  else
    null
}
