package compuquest.simulation.combat

import compuquest.simulation.general.*
import compuquest.simulation.physics.castCharacterRay
import godot.Spatial
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Events

fun useHealingSpell(world: World, recipient: Id, effect: AccessoryEffect): Events {
	val scene = effect.spawnsScene
	if (scene != null) {
		val location = world.deck.bodies[recipient]?.globalTransform?.origin
		if (location != null) {
			val node = instantiateScene<Spatial>(scene)!!
			node.translation = location
			world.scene.addChild(node)
		}
	}
	return listOfNotNull(
		modifyHealth(recipient, effect.strengthInt)
	)
}

fun raycastHeal(world: World, actor: Id, accessory: Accessory, effect: AccessoryEffect): Events {
	val definition = accessory.definition
	val recipient = castCharacterRay(world, actor, definition.range)
	return if (recipient != null) {
		useHealingSpell(world, recipient, effect)
	} else
		listOf()
}
