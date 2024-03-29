package silentorb.mythic.godoting

import compuquest.simulation.definition.ResourceType
import godot.*
import godot.core.VariantArray
import godot.global.GD

inline fun <reified T> instantiateScene(path: String): T? {
	val scene = GD.load<PackedScene>(path)
	val node = scene?.instance()
	assert(node is T)
	return node as? T
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

interface UnparentOnParentDeletion

fun deleteNode(node: Node, root: Node? = null) {
	tempCatch {
		for (i in (node.getChildCount() - 1) downTo 0) {
			val child = node.getChild(i)
			if (child is UnparentOnParentDeletion) {
				node.removeChild(child)
				root?.callDeferred("add_child", child)
			}
		}
		node.queueFree()
	}
}

inline fun <reified T> getVariantArray(value: Object?, property: String): List<T> =
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

inline fun <reified T> findChildrenOfType(node: Node): List<T> =
	findChildren(node) { it is T } as List<T>

fun findChildrenOfScriptType(scriptPath: String, node: Node): List<Node> =
	findChildren(node) { (it.getScript() as? Resource)?.resourcePath == scriptPath }

inline fun <reified T> findParentOfType(node: Node): T? {
	var current = node
	for (i in 0..100) {
		current = when (val parent = current.getParent()) {
			is T -> return parent
			null -> return null
			else -> parent
		}
	}
	throw Error("Infinite loop detected trying to find node parent of type")
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

fun getResourceType(value: Object, property: String): ResourceType? {
	val resource = getString(value, property)
	return ResourceType.values().firstOrNull { it.name == resource }
}

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

fun getShapeRadius(shape: Shape): Float =
	when (shape) {
		is CapsuleShape -> shape.radius.toFloat()
		is CylinderShape -> shape.radius.toFloat()
		is SphereShape -> shape.radius.toFloat()
		else -> 0f
	}

fun getCollisionShapeRadius(collisionShape: CollisionShape): Float {
	val shape = collisionShape.shape
	return if (shape != null)
		getShapeRadius(shape)
	else
		0f
}

inline fun <reified T> getChildrenOfType(node: Node): List<T> =
	node.getChildren().filterIsInstance<T>()

inline fun <reified T> getChildOfType(node: Node): T? =
	node.getChildren().filterIsInstance<T>().firstOrNull()

fun getFilesInDirectory(directoryPath: String, recursive: Boolean = false): List<String> {
	val directory = Directory()
	directory.open(directoryPath)
	directory.listDirBegin()
	val files = mutableListOf<String>()

	while (true) {
		val file = directory.getNext()
		if (file == "")
			break
		else if (file == "." || file == "..")
			continue
		else {
			if (directory.currentIsDir()) {
				if (recursive) {
					files.addAll(getFilesInDirectory("$directoryPath/$file"))
				}
			} else {
				files.add("$directoryPath/$file")
			}
		}
	}

	directory.listDirEnd()
	return files
}

// An alternative to InstancePlaceholder::create_instance
// because that function has the nonsensical requirement of the placeholder being in the scene tree.
// The code works just fine outside the tree.  There's nothing exotic about these operations
// and Godot has similar functions that don't require the content to be in the scene tree.
fun replacePlaceholderNode(placeholder: InstancePlaceholder): Node? {
	val parent = placeholder.getParent() ?: return null

	val scene = instantiateScene<Node>(placeholder.getInstancePath()) ?: return null
	scene.name = placeholder.name
	val position = placeholder.getPositionInParent()
	for (value in placeholder.getStoredValues()) {
		val key = value.key
		if (key != null) {
			scene.set(key.toString(), value.value)
		}
	}

	placeholder.queueFree()
	parent.removeChild(placeholder)

	parent.addChild(scene)
	parent.moveChild(scene, position)
	return scene
}
