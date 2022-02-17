package compuquest.simulation.combat

import compuquest.simulation.characters.spawnCharacter
import compuquest.simulation.general.*
import compuquest.simulation.intellect.newSpirit
import godot.PackedScene
import godot.Spatial
import godot.core.Vector3
import godot.global.GD
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Events
import silentorb.mythic.timing.newTimer

val forEachSummonEffect = forEachEffectOfType(AccessoryEffects.summon)

fun summonAtLocation(world: World, effect: AccessoryEffect, location: Vector3): Events {
	val spawnsCharacter = effect.spawnsCharacter
	val timer = if (effect.duration > 0f)
		newTimer(effect.duration)
	else
		null

	return if (spawnsCharacter != null) {
		val scene = effect.spawnsScene ?: "res://entities/actor/ActorBodyCapsule.tscn"
		val actor = world.nextId.source()()
		newHandEvents(
			spawnCharacter(
				world, GD.load(scene)!!, location, Vector3.ZERO, spawnsCharacter,
				additional = listOf(newSpirit()), id = actor
			) + listOf(
				Hand(
					id = actor,
					components = listOfNotNull(timer),
				)
			)
		)
	} else {
		val summoned = instantiateScene<Spatial>(effect.spawnsScene!!)!!
		summoned.translation = location
		listOf(
			newHandEvent(
				Hand(
					components = listOfNotNull(
						summoned,
						timer,
					)
				)
			)
		)
	}
}

fun summonInFrontOfActor(world: World, actor: Id, accessory: Accessory): Events {
	val (origin, _) = getAttackerOriginAndFacing(world, actor, null, null, 1.6f)
	return if (origin != null) {
		forEachSummonEffect(accessory.definition) { summonAtLocation(world, it, origin) }
	} else
		listOf()
}
