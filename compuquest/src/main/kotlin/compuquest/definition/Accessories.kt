package compuquest.definition

import compuquest.simulation.general.*
import silentorb.mythic.ent.KeyTable

object Accessories {
	val burning = "burning"
	val fireRing = "fireRing"
	val rifle = "rifle"
	val rocketLauncher = "rocketLauncher"
}

fun actionDefinitions(): KeyTable<AccessoryDefinition> = mapOf(
	Accessories.fireRing to AccessoryDefinition(
		name = Accessories.fireRing,
		cooldown = 3f,
		attributes = setOf(AccessoryAttributes.attack),
		range = 15f,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summonAtTarget,
				strength = 20f,
				duration = 4f,
				spawns = "res://entities/effect/FireRing.tscn",
			),
		)
	),
	Accessories.rifle to AccessoryDefinition(
		name = Accessories.rifle,
		cooldown = 0.2f,
		attributes = setOf(AccessoryAttributes.attack),
		range = 30f,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.attack,
				strength = 10f,
				spawns = "res://entities/effect/Fireball.tscn",
				speed = 60f,
			),
		)
	),
	Accessories.rocketLauncher to AccessoryDefinition(
		name = Accessories.rocketLauncher,
		cooldown = 1f,
		attributes = setOf(AccessoryAttributes.attack),
		range = 15f,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.attack,
				strength = 20f,
				spawns = "res://entities/effect/Fireball.tscn",
				speed = 20f,
			),
		)
	),
)

fun buffDefinitions(): KeyTable<AccessoryDefinition> = mapOf(
	Accessories.burning to AccessoryDefinition(
		name = Accessories.burning,
		duration = 4f,
		passiveEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damageSelf,
				strength = 10f,
				interval = AccessoryIntervals.default,
			),
		)
	),
)

fun accessoryDefinitions(): KeyTable<AccessoryDefinition> =
	actionDefinitions() + buffDefinitions()
