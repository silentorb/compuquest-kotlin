package compuquest.definition

import compuquest.simulation.general.*
import silentorb.mythic.ent.KeyTable

object Accessories {
	val berries = "berries"
	val burning = "burning"
	val fireRing = "fireRing"
	val rifle = "rifle"
	val rocketLauncher = "rocketLauncher"
}

fun actionDefinitions(): KeyTable<AccessoryDefinition> = mapOf(
	Accessories.berries to AccessoryDefinition(
		name = Accessories.berries,
		consumable = true,
		wieldingFrame = 13,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.heal,
				recipient = EffectRecipient.self,
				strength = 10f,
			),
		)
	),
	Accessories.fireRing to AccessoryDefinition(
		name = Accessories.fireRing,
		cooldown = 8f,
		attributes = setOf(AccessoryAttributes.attack),
		range = 15f,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summonAtTarget,
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
				strength = 4f,
				interval = AccessoryIntervals.default,
			),
		)
	),
)

fun accessoryDefinitions(): KeyTable<AccessoryDefinition> =
	actionDefinitions() + buffDefinitions()
