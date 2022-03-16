package scripts.entities

import compuquest.population.nextLevelEvent
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.happening.newEvent

@RegisterClass
class NextLevelPortal : Spatial() {
	var step = 0

	val triggerDistance = 3f
	val updateInterval = 10

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		if (step++ > updateInterval) {
			step = 0
			val world = Global.world
			if (world != null) {
				val deck = world.deck
				val origin = globalTransform.origin
				val isNearby = deck.players.keys.any { actor ->
					val distance = deck.bodies[actor]?.globalTransform?.origin?.distanceTo(origin)
					distance != null && distance < triggerDistance
				}
				if (isNearby) {
					Global.addEvent(newEvent(nextLevelEvent))
				}
			}
		}
	}

}
