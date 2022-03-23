package compuquest.definition

import compuquest.simulation.characters.*
import silentorb.mythic.ent.KeyTable

fun actionDefinitions(): KeyTable<AccessoryDefinition> = listOf(
	AccessoryDefinition(
		key = Accessories.berries,
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
	AccessoryDefinition(
		key = Accessories.bite,
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
	AccessoryDefinition(
		key = Accessories.burger,
		consumable = true,
		equippedFrame = EquipmentFrames.burger,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.heal,
				recipient = EffectRecipient.self,
				strength = 30f,
			),
		)
	),
	AccessoryDefinition(
		key = Accessories.fireRing,
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
	AccessoryDefinition(
		key = Accessories.heal,
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
	AccessoryDefinition(
		key = Accessories.invisibility,
		cooldown = 3f,
		equippedFrame = EquipmentFrames.invisibility,
		cooldownDelayEffect = Accessories.invisible,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.buff,
				recipient = EffectRecipient.self,
				buff = Accessories.invisible,
				duration = 4f,
			),
			AccessoryEffect(
				type = AccessoryEffects.equipPrevious,
				recipient = EffectRecipient.self,
			),
		)
	),
	AccessoryDefinition(
		key = Accessories.rifle,
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
	AccessoryDefinition(
		key = Accessories.fireStaff,
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
	AccessoryDefinition(
		key = Accessories.sai,
		cooldown = 1f,
		range = 3f,
		equippedFrame = EquipmentFrames.sai,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				strength = 30f,
				spawnsScene = "res://entities/effect/Bite.tscn",
				speed = 15f,
			),
		)
	),
	AccessoryDefinition(
		key = Accessories.summonFox,
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
	AccessoryDefinition(
		key = Accessories.summonSquid,
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
).associateBy { it.key }

val removeOnUseAny = AccessoryEffect(
	type = AccessoryEffects.removeOnUseAny,
	interval = AccessoryIntervals.continuous,
	recipient = EffectRecipient.self,
)

fun buffDefinitions(): KeyTable<AccessoryDefinition> = listOf(
	AccessoryDefinition(
		key = Accessories.backstab,
		passiveEffects = newPassiveEffects(
			AccessoryEffects.backstab,
		),
	),
	AccessoryDefinition(
		key = Accessories.burning,
		duration = 4f,
		passiveEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.self,
				strength = 4f,
				interval = AccessoryIntervals.default,
			),
		),
	),
	AccessoryDefinition(
		key = Accessories.invisible,
		passiveEffects = listOf(
			newPassiveEffect(AccessoryEffects.invisible),
			removeOnUseAny,
		),
	),
).associateBy { it.key }

fun accessoryDefinitions(): KeyTable<AccessoryDefinition> =
	actionDefinitions() + buffDefinitions()
