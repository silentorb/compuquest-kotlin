package compuquest.simulation.combat

import compuquest.simulation.characters.*
import compuquest.simulation.general.*
import compuquest.simulation.intellect.knowledge.Personality
import compuquest.simulation.intellect.newSpirit
import compuquest.simulation.physics.castRay
import godot.Spatial
import godot.core.Transform
import godot.core.Vector3
import godot.global.GD
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Events
import silentorb.mythic.timing.newTimer

val forEachSummonEffect = forEachEffectOfType(AccessoryEffects.summon)

fun summonAtLocation(world: World, actor: Id, effect: AccessoryEffect, transform: Transform): Events {
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
				world, GD.load(scene)!!, transform, characterType,
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
		val (_, groundLocation) = castRay(world, transform.origin + Vector3.UP, transform.origin + Vector3(0, -10, 0))

		summoned.translation = if (groundLocation != null)
			groundLocation
		else
			transform.origin

		val summonerRotation = world.deck.bodies[actor]?.rotation
		if (summonerRotation != null) {
			summoned.rotation {
				y = summonerRotation.y
			}
		}

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
		forEachSummonEffect(accessory.definition) { summonAtLocation(world, actor, it, Transform().translated(origin)) }
	} else
		listOf()
}
