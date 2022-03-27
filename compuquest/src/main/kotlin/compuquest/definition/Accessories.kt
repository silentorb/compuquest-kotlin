package compuquest.definition

import compuquest.simulation.characters.*
import compuquest.simulation.combat.damagesOf
import godot.core.Transform
import godot.core.Vector3
import godot.global.PI

object AccessoryDefinitions {

	val berries = AccessoryDefinition(
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
	)

	val bite = AccessoryDefinition(
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
	)

	val burger = AccessoryDefinition(
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
	)

	val fireRing = AccessoryDefinition(
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
	)

	val grenade = AccessoryDefinition(
		key = Accessories.grenade,
		cooldown = 0.6f,
		useRange = 10f,
		equippedFrame = EquipmentFrames.banana,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				damages = damagesOf(
					DamageTypes.fire to 10,
					DamageTypes.physical to 10,
				),
				spawnsScene = "res://entities/effect/Grenade.tscn",
				duration = 1.5f,
				damageRadius = 2f,
				damageFalloff = 0.8f,
				speed = 30f,
				spawnOnEnd = "res://entities/effect/Explosion.tscn",
				transform = Transform().translated(Vector3(0, 0, -1)).rotated(Vector3.RIGHT, PI / 5),
			),
		)
	)

	val heal = AccessoryDefinition(
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
	)
	val invisibility = AccessoryDefinition(
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
	)

	val rifle = AccessoryDefinition(
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
	)

	val fireStaff = AccessoryDefinition(
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
	)

	val sai = AccessoryDefinition(
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
	)

	val summonFox = AccessoryDefinition(
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
	)
	val summonIceWall = AccessoryDefinition(
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
	)

	val summonSquid = AccessoryDefinition(
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
	)
}

object BuffDefinitions {

	val backstab = AccessoryDefinition(
		key = Accessories.backstab,
		passiveEffects = newPassiveEffects(
			AccessoryEffects.backstab,
		),
	)

	val burning = AccessoryDefinition(
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
	)

	val invisible = AccessoryDefinition(
		key = Accessories.invisible,
		passiveEffects = listOf(
			newPassiveEffect(AccessoryEffects.invisible),
			removeOnUseAny,
		),
	)
}
