package scripts.entities

import compuquest.definition.Accessories
import compuquest.simulation.general.getBodyEntityId
import compuquest.simulation.general.newAccessory
import compuquest.simulation.general.newHandEvent
import godot.Area
import godot.CollisionShape
import godot.Node
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.godoting.getCollisionShapeRadius

@RegisterClass
class FireRing : Spatial() {

	var bounds: Area? = null
	var radius: Float = 0f

	@RegisterFunction
	override fun _ready() {
		bounds = findNode("bounds") as Area
		val collisionShape = findNode("shape") as CollisionShape
		radius = getCollisionShapeRadius(collisionShape)
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		val world = Global.world
		if (world != null) {
			val location = globalTransform.origin
			val collisions = bounds!!.getOverlappingBodies().filterIsInstance<CharacterBody>()
			for (collision in collisions) {
				val bodyRadius = collision.radius
				val distance = collision.location.distanceTo(location)
				if (distance > radius - bodyRadius) {
					val actor = getBodyEntityId(world.deck, collision as Node)
					if (actor != null) {
						Global.addEvent(
							newHandEvent(
								newAccessory(world.definitions, world.nextId.source(), actor, Accessories.burning)
							)
						)
					}
				}
			}
		}
	}
}
