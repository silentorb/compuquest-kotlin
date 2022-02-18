package compuquest.definition

import compuquest.simulation.general.*
import silentorb.mythic.ent.KeyTable

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
				recipient = EffectRecipient.projectile,
				strength = 8f,
				spawnsScene = "res://entities/effect/Bite.tscn",
				speed = 15f,
			),
		)
	),
	Accessories.fireRing to AccessoryDefinition(
		name = Accessories.fireRing,
		cooldown = 8f,
		range = 15f,
		equippedFrame = EquipmentFrames.fireStaff,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summonAtTarget,
				recipient = EffectRecipient.projectile,
				duration = 4f,
				spawnsScene = "res://entities/effect/FireRing.tscn",
			),
		)
	),
	Accessories.heal to AccessoryDefinition(
		name = Accessories.heal,
		cooldown = 1.5f,
		range = 6f,
		equippedFrame = EquipmentFrames.heal,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.heal,
				recipient = EffectRecipient.raycast,
				strength = 25f,
				spawnsScene = "res://entities/effect/Heal.tscn",
			),
		)
	),
	Accessories.rifle to AccessoryDefinition(
		name = Accessories.rifle,
		cooldown = 0.2f,
		range = 30f,
		equippedFrame = EquipmentFrames.fireStaff,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				strength = 10f,
				spawnsScene = "res://entities/effect/Fireball.tscn",
				speed = 60f,
			),
		)
	),
	Accessories.fireStaff to AccessoryDefinition(
		name = Accessories.fireStaff,
		cooldown = 1f,
		range = 15f,
		equippedFrame = EquipmentFrames.fireStaff,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				strength = 20f,
				spawnsScene = "res://entities/effect/Fireball.tscn",
				speed = 20f,
			),
		)
	),
	Accessories.summonFox to AccessoryDefinition(
		name = Accessories.summonFox,
		cooldown = 5f,
		range = 1f,
		equippedFrame = EquipmentFrames.summonFox,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summon,
				recipient = EffectRecipient.inFront,
				spawnsCharacter = Characters.fox,
				duration = 19f
			),
		)
	),
	Accessories.summonSquid to AccessoryDefinition(
		name = Accessories.summonSquid,
		cooldown = 5f,
		range = 1f,
		equippedFrame = EquipmentFrames.summonSquid,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summon,
				recipient = EffectRecipient.inFront,
				spawnsCharacter = Characters.squid,
				duration = 19f
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
