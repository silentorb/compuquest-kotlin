package compuquest.simulation.combat

import compuquest.simulation.general.Accessory
import compuquest.simulation.general.Hand
import compuquest.simulation.general.World
import compuquest.simulation.general.newHandEvent
import godot.Spatial
import godot.core.Vector3
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Events
import silentorb.mythic.timing.newTimer

fun summonAtTarget(world: World, actor: Id, weapon: Accessory, targetLocation: Vector3): Events {
	val definition = weapon.definition
	val effect = definition.actionEffects.first()
	val summoned = instantiateScene<Spatial>(effect.spawns!!)!!
	summoned.translation = targetLocation

	return listOf(
		newHandEvent(
			Hand(
				components = listOf(
					summoned,
					newTimer(weapon.definition.duration),
				)
			)
		)
	)
}
