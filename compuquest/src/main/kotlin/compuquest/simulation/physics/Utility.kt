package compuquest.simulation.physics

import godot.Spatial
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.ent.TableEntry

fun getLocation(spatial: Spatial): Vector3 =
	(spatial as? CharacterBody)?.location ?: spatial.globalTransform.origin

fun getNearest(bodies: List<TableEntry<Spatial>>, location: Vector3): TableEntry<Spatial>? =
	if (bodies.none())
		null
	else {
		var current = bodies.first()
		var currentDistance = location.distanceTo(getLocation(current.value))
		var i = 0
		for (body in bodies) {
			if (i++ == 0) continue
			val distance = location.distanceTo(getLocation(body.value))
			if (distance < currentDistance) {
				current = body
				currentDistance = distance
			}
		}
		current
	}
