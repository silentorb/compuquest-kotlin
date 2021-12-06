package scripts.entities

import godot.Area
import godot.KinematicBody
import godot.RigidBody
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector3
import silentorb.mythic.godoting.deleteNode

@RegisterClass
class Projectile : RigidBody() {

  @RegisterProperty
  var velocity: Vector3 = Vector3.ZERO

  @RegisterProperty
  var range: Float = 0f

  var origin: Vector3 = Vector3.ZERO
  var ignore: Any? = null

  @RegisterFunction
  override fun _ready() {
	origin = transform.origin
  }

  @RegisterFunction
  override fun _physicsProcess(delta: Double) {
	val collisions = getCollidingBodies()
	if (collisions.any() || origin.distanceTo(transform.origin) > range) {
	  if (collisions.none()) {
		val k = 0
	  }
	visible = false
	  deleteNode(this)
	}
//	translate()
//	val hit = moveAndCollide(velocity * delta)
//	if (hit != null || origin.distanceTo(transform.origin) > range) {
//	  if (hit == null) {
//		val k = 0
//	  }
//	  deleteNode(this)
//	}
//	val collisions = getOverlappingBodies()
//	val hit = collisions.any { it != ignore }
//	if (hit || origin.distanceTo(transform.origin) > range) {
//	  if (!hit) {
//		val k = 0
//	  }
//	  deleteNode(this)
//	}
  }
}
