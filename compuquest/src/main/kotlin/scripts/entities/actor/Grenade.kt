package scripts.entities.actor

import compuquest.clienting.audio.playSound
import compuquest.definition.Sounds
import godot.Node
import godot.RigidBody
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.audio.Sound

@RegisterClass
class Grenade : RigidBody() {
	var gap = 0

	@RegisterFunction
	fun on_collide(body: Node) {
		if (gap == 0) {
			gap = 10
			Global.addEvent(
				playSound(
					Sound(
						type = Sounds.metalBounce,
						volume = 0.03f,
						location = globalTransform.origin,
					)
				)
			)
		}
	}

	@RegisterFunction
	override fun _ready() {
		connect("body_entered", this, "on_collide")
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		if (gap > 0) --gap
	}
}
