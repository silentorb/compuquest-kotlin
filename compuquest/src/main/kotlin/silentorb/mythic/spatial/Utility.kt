package silentorb.mythic.spatial

import godot.core.Vector2
import godot.core.Vector3
import kotlin.math.PI

const val Pi = PI.toFloat()

fun minMax(min: Int, max: Int, value: Int) =
	when {
		value < min -> min
		value > max -> max
		else -> value
	}

fun minMax(value: Float, min: Float, max: Float): Float =
	when {
		value < min -> min
		value > max -> max
		else -> value
	}

fun atan(v: Vector2) =
	Math.atan2(v.y.toDouble(), v.x.toDouble()).toFloat()

fun getAngle(a: Vector2, b: Vector2): Float {
	val ad = atan(a)
	val bd = atan(b)
	return bd - ad
}

fun getAngle(a: Float, b: Vector2): Float {
	val bd = atan(b)
	return bd - a
}

private val baseYawAngle = atan(Vector2(0f, -1f))

fun getYawAngle(lookAt: Vector2): Float =
	-getAngle(baseYawAngle, lookAt)

fun getYawAngle(lookAt: Vector3): Float =
	getYawAngle(lookAt.xz())

fun getPitchAngle(lookAt: Vector3) =
	-getAngle(Vector2(1f, 0f), Vector2(lookAt.xz().length(), lookAt.z))

fun getYawAndPitch(lookAt: Vector3): Pair<Float, Float> =
	Pair(
		getYawAngle(lookAt),
		getPitchAngle(lookAt)
	)

fun getYawVector(lookAt: Vector2): Vector3 =
	Vector3(0, getYawAngle(lookAt), 0)

fun getYawVector(lookAt: Vector3): Vector3 =
	Vector3(0, getYawAngle(lookAt), 0)
