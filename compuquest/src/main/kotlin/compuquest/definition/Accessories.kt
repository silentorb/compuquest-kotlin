package compuquest.definition

import compuquest.simulation.characters.*
import compuquest.simulation.combat.damagesOf
import godot.core.Transform
import godot.core.Vector3
import godot.global.PI
import silentorb.mythic.ent.KeyTable

val summonOffset = Transform().translated(Vector3(0, 0, -1.6))

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
		useRange = 5f,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				range = 5f,
				damages = damagesOf(
					DamageTypes.physical to 8,
				),
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
		useRange = 15f,
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
		key = Accessories.banana,
		cooldown = 0.6f,
		useRange = 10f, // Only used for AI
		equippedFrame = EquipmentFrames.banana,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				damages = damagesOf(
					DamageTypes.fire to 15,
					DamageTypes.physical to 15,
				),
				spawnsScene = "res://entities/effect/Grenade.tscn",
				duration = 2.5f,
				speed = 30f,
				transform = Transform().translated(Vector3(0, 0, -1)).rotated(Vector3.RIGHT, PI / 5),
			),
		)
	),
	AccessoryDefinition(
		key = Accessories.heal,
		cooldown = 1.5f,
		useRange = 6f,
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
		useRange = 30f,
		equippedFrame = EquipmentFrames.fireStaff,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				range = 30f,
				damages = damagesOf(
					DamageTypes.physical to 10,
				),
				spawnsScene = "res://entities/effect/Fireball.tscn",
				speed = 60f,
			),
		)
	),
	AccessoryDefinition(
		key = Accessories.fireStaff,
		cooldown = 1f,
		useRange = 15f,
		equippedFrame = EquipmentFrames.fireStaff,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				damages = damagesOf(
					DamageTypes.fire to 20,
				),
				spawnsScene = "res://entities/effect/Fireball.tscn",
				speed = 20f,
			),
		)
	),
	AccessoryDefinition(
		key = Accessories.sai,
		cooldown = 1f,
		useRange = 3f,
		equippedFrame = EquipmentFrames.sai,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				range = 3f,
				damages = damagesOf(
					DamageTypes.physical to 30,
				),
				spawnsScene = "res://entities/effect/Bite.tscn",
				speed = 15f,
			),
		)
	),
	AccessoryDefinition(
		key = Accessories.summonFox,
		cooldown = 5f,
		useRange = 1f,
		equippedFrame = EquipmentFrames.summonFox,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summon,
				recipient = EffectRecipient.inFront,
				spawnsCharacter = Characters.fox,
				duration = 19f,
				transform = summonOffset,
			),
		)
	),
	AccessoryDefinition(
		key = Accessories.summonIceWall,
		cooldown = 7f,
		useRange = 1f,
		equippedFrame = EquipmentFrames.summonIceWall,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summon,
				recipient = EffectRecipient.inFront,
				spawnsScene = "res://entities/actor/IceWall.tscn",
				duration = 5f,
				transform = summonOffset,
			),
		)
	),
	AccessoryDefinition(
		key = Accessories.summonSquid,
		cooldown = 5f,
		useRange = 1f,
		equippedFrame = EquipmentFrames.summonSquid,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summon,
				recipient = EffectRecipient.inFront,
				spawnsCharacter = Characters.squid,
				duration = 14f,
				transform = summonOffset,
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
