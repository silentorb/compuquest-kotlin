package compuquest.simulation.combat

import compuquest.simulation.characters.Accessory
import compuquest.simulation.characters.AccessoryEffect
import compuquest.simulation.characters.getProficienceyModifier
import compuquest.simulation.general.*
import compuquest.simulation.physics.castRayForId
import godot.Spatial
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Events

fun useHealingSpell(world: World, recipient: Id, effect: AccessoryEffect, mod: Int100): Events {
	val deck =world.deck
	val scene = effect.spawnsScene
	if (scene != null) {
		val location = deck.bodies[recipient]?.globalTransform?.origin
		if (location != null) {
			val node = instantiateScene<Spatial>(scene)!!
			node.translation = location
			world.scene.addChild(node)
		}
	}
	return listOfNotNull(
		modifyHealth(recipient, effect.getStrength(mod))
	)
}

fun raycastHeal(world: World, actor: Id, accessory: Accessory, effect: AccessoryEffect): Events {
	val definition = accessory.definition
	val recipient = castRayForId(world, actor, definition.useRange)
	return if (recipient != null)
		useHealingSpell(world, recipient, effect, getProficienceyModifier(world.deck, effect, actor))
	else
		listOf()
}
