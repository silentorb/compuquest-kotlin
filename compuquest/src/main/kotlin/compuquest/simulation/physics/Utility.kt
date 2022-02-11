package compuquest.simulation.physics

import godot.Spatial
import godot.core.Vector3
import silentorb.mythic.ent.TableEntry

fun getNearest(bodies: List<TableEntry<Spatial>>, location: Vector3): TableEntry<Spatial>? =
	if (bodies.none())
		null
	else {
		var current = bodies.first()
		var currentDistance = location.distanceTo(current.value.globalTransform.origin)
		var i = 0
		for (body in bodies) {
			if (i++ == 0) continue
			val distance = location.distanceTo(body.value.globalTransform.origin)
			if (distance < currentDistance) {
				current = body
				currentDistance = distance
			}
		}
		current
	}
