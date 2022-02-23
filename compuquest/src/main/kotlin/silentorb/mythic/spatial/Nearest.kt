package silentorb.mythic.spatial

import godot.core.Vector2
import godot.core.Vector3
import kotlin.math.abs

fun withinRange(a: Vector3, b: Vector3, range: Float): Boolean =
	a.distanceTo(b) <= range

// Faster than a regular distance check
fun withinRangeFast(a: Vector3, b: Vector3, range: Float): Boolean {
//  assert(a.z == b.z) // This function only works along two dimensions.  What was I thinking?
	val c = abs(a.x - b.x)
	if (c > range) {
		return false
	}

	val d = abs(a.y - b.y)
	if (d > range) {
		return false
	}

	val m = c + d

	return if (m <= range) {
		true
	} else if (m * 0.70710677 <= range) {
		a.distanceTo(b) <= range
	} else {
		false
	}
}

// Faster than a regular distance check
fun withinRangeFast(a: Vector2, b: Vector2, range: Float): Boolean {
//  assert(a.z == b.z) // This function only works along two dimensions.  What was I thinking?
	val c = abs(a.x - b.x)
	if (c > range) {
		return false
	}

	val d = abs(a.y - b.y)
	if (d > range) {
		return false
	}

	val m = c + d

	return if (m <= range) {
		true
	} else if (m * 0.70710677 <= range) {
		a.distanceTo(b) <= range
	} else {
		false
	}
}

fun <T> nearest(items: List<Pair<Vector3, T>>): (Vector3) -> T = { anchor ->
	var result = items.first()
	var distance = result.first.distanceTo(anchor).toFloat()
	for (item in items.asSequence().drop(1)) {
		if (withinRange(anchor, item.first, distance)) {
			result = item
			distance = item.first.distanceTo(anchor).toFloat()
		}
	}
	result.second
}

fun <T> nearestFast2d(items: List<Pair<Vector2, T>>): (Vector2) -> T = { anchor ->
	var result = items.first()
	var distance = result.first.distanceTo(anchor).toFloat()
	for (item in items.asSequence().drop(1)) {
		if (withinRangeFast(anchor, item.first, distance)) {
			result = item
			distance = item.first.distanceTo(anchor).toFloat()
		}
	}
	result.second
}
