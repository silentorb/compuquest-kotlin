package compuquest.simulation.combat

import compuquest.simulation.characters.Relationship
import compuquest.simulation.characters.RelationshipType
import compuquest.simulation.characters.spawnCharacter
import compuquest.simulation.general.*
import compuquest.simulation.intellect.knowledge.Personality
import compuquest.simulation.intellect.newSpirit
import godot.Spatial
import godot.core.Vector3
import godot.global.GD
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Events
import silentorb.mythic.timing.newTimer

val forEachSummonEffect = forEachEffectOfType(AccessoryEffects.summon)

fun summonAtLocation(world: World, actor: Id, effect: AccessoryEffect, location: Vector3): Events {
	val characterType = effect.spawnsCharacter
	val timer = if (effect.duration > 0f)
		newTimer(effect.duration)
	else
		null

	return if (characterType != null) {
		val scene = effect.spawnsScene ?: "res://entities/actor/ActorBodyCapsule.tscn"
		val summonedActor = world.nextId.source()()
		val definition = world.definitions.characters[characterType]
		val personality = definition?.personality ?: Personality()
		newHandEvents(
			spawnCharacter(
				world, GD.load(scene)!!, location, Vector3.ZERO, characterType,
				relationships = listOf(Relationship(RelationshipType.master, actor)),
				additional = listOf(newSpirit(personality)),
				id = summonedActor
			) + listOf(
				Hand(
					id = summonedActor,
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
		forEachSummonEffect(accessory.definition) { summonAtLocation(world, actor, it, origin) }
	} else
		listOf()
}
