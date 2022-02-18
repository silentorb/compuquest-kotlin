package scripts.entities

import godot.Spatial
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty

@RegisterClass
class ExpirationTimer : Spatial() {

	@Export
	@RegisterProperty
	var duration: Float = 1f

	var accumulator: Float = 0f

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		accumulator += delta.toFloat()
		if (accumulator >= duration) {
			queueFree()
		}
	}
}
