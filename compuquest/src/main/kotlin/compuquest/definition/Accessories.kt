package compuquest.definition

import compuquest.simulation.general.*
import silentorb.mythic.ent.KeyTable

object Accessories {
	val berries = "berries"
	val bite = "bite"
	val burning = "burning"
	val fireRing = "fireRing"
	val rifle = "rifle"
	val summonFox = "summonFox"
	val rocketLauncher = "rocketLauncher"
}

object EquipmentFrames {
	const val berries = 0
	const val fireball = 1
	const val summonFox = 2
}

fun actionDefinitions(): KeyTable<AccessoryDefinition> = mapOf(
	Accessories.berries to AccessoryDefinition(
		name = Accessories.berries,
		consumable = true,
		equippedFrame = EquipmentFrames.berries,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.heal,
				recipient = EffectRecipient.self,
				strength = 10f,
			),
		)
	),
	Accessories.bite to AccessoryDefinition(
		name = Accessories.bite,
		cooldown = 0.6f,
		range = 5f,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				strength = 6f,
				spawnsScene = "res://entities/effect/Bite.tscn",
				speed = 60f,
			),
		)
	),
	Accessories.fireRing to AccessoryDefinition(
		name = Accessories.fireRing,
		cooldown = 8f,
		range = 15f,
		equippedFrame = EquipmentFrames.fireball,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summonAtTarget,
				duration = 4f,
				spawnsScene = "res://entities/effect/FireRing.tscn",
			),
		)
	),
	Accessories.rifle to AccessoryDefinition(
		name = Accessories.rifle,
		cooldown = 0.2f,
		range = 30f,
		equippedFrame = EquipmentFrames.fireball,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				strength = 10f,
				spawnsScene = "res://entities/effect/Fireball.tscn",
				speed = 60f,
			),
		)
	),
	Accessories.rocketLauncher to AccessoryDefinition(
		name = Accessories.rocketLauncher,
		cooldown = 1f,
		range = 15f,
		equippedFrame = EquipmentFrames.fireball,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				strength = 20f,
				spawnsScene = "res://entities/effect/Fireball.tscn",
				speed = 20f,
			),
		)
	),
	Accessories.summonFox to AccessoryDefinition(
		name = Accessories.summonFox,
		cooldown = 0.2f,
		range = 1f,
		equippedFrame = EquipmentFrames.summonFox,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summon,
				spawnsCharacter = Characters.fox,
				duration = 60f
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
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.self,
				strength = 4f,
				interval = AccessoryIntervals.default,
			),
		)
	),
)

fun accessoryDefinitions(): KeyTable<AccessoryDefinition> =
	actionDefinitions() + buffDefinitions()
