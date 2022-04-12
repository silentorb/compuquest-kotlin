package compuquest.definition

import compuquest.simulation.characters.*
import compuquest.simulation.combat.damagesOf
import godot.core.Transform
import godot.core.Vector3
import godot.global.PI
import silentorb.mythic.localization.DevText

object AccessoryDefinitions {

	val berries = AccessoryDefinition(
		key = Accessories.berries,
		slot = AccessorySlot.consumable,
		equippedFrame = EquipmentFrames.berries,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.heal,
				recipient = EffectRecipient.self,
				strength = 10f,
			),
		),
		description = DevText("Provides a small amount of nutrition."),
	)

	val bite = AccessoryDefinition(
		key = Accessories.bite,
		slot = AccessorySlot.primary,
		cooldown = 0.6f,
		useRange = 5f,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				proficiencies = setOf(Proficiencies.closeCombat, Proficiencies.nature),
				recipient = EffectRecipient.projectile,
				range = 5f,
				damages = damagesOf(
					DamageTypes.physical to 8,
				),
				spawnsScene = "res://entities/effect/Bite.tscn",
				speed = 15f,
			),
		),
		description = DevText("Deals damage"),
	)

	val burger = AccessoryDefinition(
		key = Accessories.burger,
		slot = AccessorySlot.consumable,
		equippedFrame = EquipmentFrames.burger,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.heal,
				recipient = EffectRecipient.self,
				strength = 30f,
			),
		)
	)

	val energyStaff = AccessoryDefinition(
		key = Accessories.energyStaff,
		slot = AccessorySlot.primary,
		cooldown = 0.15f,
		useRange = 25f,
		equippedFrame = EquipmentFrames.energyStaff,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				proficiencies = setOf(Proficiencies.rangedCombat, Proficiencies.magic),
				range = 25f,
				damages = damagesOf(
					DamageTypes.magic to 8,
				),
				spawnsScene = "res://entities/effect/EnergyBall.tscn",
				speed = 60f,
				sound = Sounds.shootEnergy,
			),
		)
	)

	val grenade = AccessoryDefinition(
		key = Accessories.grenade,
		slot = AccessorySlot.utility,
		cooldown = 3f,
		useRange = 10f,
		equippedFrame = EquipmentFrames.banana,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				proficiencies = setOf(Proficiencies.rangedCombat, Proficiencies.mechanics),
				damages = damagesOf(
					DamageTypes.fire to 30,
					DamageTypes.physical to 30,
				),
				spawnsScene = "res://entities/effect/Grenade.tscn",
				duration = 1.5f,
				damageRadius = 5f,
				damageFalloff = 0.8f,
				speed = 30f,
				spawnOnEnd = "res://entities/effect/Explosion.tscn",
				transform = Transform().translated(Vector3(0, 0, -1)).rotated(Vector3.RIGHT, PI / 5),
			),
		)
	)

	val heal = AccessoryDefinition(
		key = Accessories.heal,
		slot = AccessorySlot.primary,
		cooldown = 1.5f,
		useRange = 6f,
		equippedFrame = EquipmentFrames.heal,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.heal,
				recipient = EffectRecipient.raycast,
				proficiencies = setOf(Proficiencies.nature),
				strength = 25f,
				spawnsScene = "res://entities/effect/Heal.tscn",
			),
		)
	)

	val invisibility = AccessoryDefinition(
		key = Accessories.invisibility,
		slot = AccessorySlot.utility,
		cooldown = 3f,
		equippedFrame = EquipmentFrames.invisibility,
		cooldownDelayEffect = Accessories.invisible,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.buff,
				recipient = EffectRecipient.self,
				proficiencies = setOf(Proficiencies.cunning),
				buff = Accessories.invisible,
				duration = 4f,
				sound = Sounds.vanish,
			),
			AccessoryEffect(
				type = AccessoryEffects.equipPrevious,
				recipient = EffectRecipient.self,
			),
		)
	)

	val jump = AccessoryDefinition(
		key = Accessories.jump,
		slot = AccessorySlot.mobility,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.jump,
				recipient = EffectRecipient.self,
			),
		)
	)

	val mortar = AccessoryDefinition(
		key = Accessories.mortar,
		slot = AccessorySlot.primary,
		cooldown = 0.6f,
		useRange = 10f,
		equippedFrame = EquipmentFrames.banana,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				proficiencies = setOf(Proficiencies.rangedCombat, Proficiencies.mechanics),
				damages = damagesOf(
					DamageTypes.fire to 10,
					DamageTypes.physical to 10,
				),
				spawnsScene = "res://entities/effect/Grenade.tscn",
				duration = 1.5f,
				damageRadius = 3f,
				damageFalloff = 0.8f,
				speed = 30f,
				spawnOnEnd = "res://entities/effect/Explosion.tscn",
				transform = Transform().translated(Vector3(0, 0, -1)).rotated(Vector3.RIGHT, PI / 5),
			),
		)
	)

	val sai = AccessoryDefinition(
		key = Accessories.sai,
		slot = AccessorySlot.primary,
		cooldown = 1f,
		useRange = 3f,
		equippedFrame = EquipmentFrames.sai,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				proficiencies = setOf(Proficiencies.cunning, Proficiencies.closeCombat),
				range = 3f,
				damages = damagesOf(
					DamageTypes.physical to 30,
				),
				spawnsScene = "res://entities/effect/Bite.tscn",
				speed = 15f,
				sound = Sounds.swishAttack,
			),
		),
	)

	val summonFox = AccessoryDefinition(
		key = Accessories.summonFox,
		slot = AccessorySlot.utility,
		cooldown = 5f,
		useRange = 1f,
		equippedFrame = EquipmentFrames.summonFox,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summon,
				recipient = EffectRecipient.inFront,
				proficiencies = setOf(Proficiencies.nature),
				spawnsCharacter = Characters.fox,
				duration = 19f,
				transform = summonOffset,
			),
		)
	)
	val summonIceWall = AccessoryDefinition(
		key = Accessories.summonIceWall,
		slot = AccessorySlot.utility,
		cooldown = 7f,
		useRange = 1f,
		equippedFrame = EquipmentFrames.summonIceWall,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summon,
				proficiencies = setOf(Proficiencies.magic),
				recipient = EffectRecipient.inFront,
				spawnsScene = "res://entities/actor/IceWall.tscn",
				duration = 5f,
				transform = summonOffset,
			),
		)
	)

	val summonSquid = AccessoryDefinition(
		key = Accessories.summonSquid,
		slot = AccessorySlot.utility,
		cooldown = 5f,
		useRange = 1f,
		equippedFrame = EquipmentFrames.summonSquid,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summon,
				proficiencies = setOf(Proficiencies.nature),
				recipient = EffectRecipient.inFront,
				spawnsCharacter = Characters.squid,
				duration = 14f,
				transform = summonOffset,
			),
		)
	)

	val sword = AccessoryDefinition(
		key = Accessories.sword,
		slot = AccessorySlot.primary,
		cooldown = 0.6f,
		useRange = 3f,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				proficiencies = setOf(Proficiencies.closeCombat),
				recipient = EffectRecipient.projectile,
				range = 5f,
				damages = damagesOf(
					DamageTypes.physical to 15,
				),
				spawnsScene = "res://entities/effect/Bite.tscn",
				speed = 15f,
				sound = Sounds.swishAttack,
			),
		)
	)

}

object PassiveDefinitions {

	val backstab = AccessoryDefinition(
		key = Accessories.backstab,
		slot = AccessorySlot.passive,
		passiveEffects = listOf(
			newPassiveEffect(AccessoryEffects.backstab, setOf(Proficiencies.cunning)),
		),
	)

}

object AiOnlyAccessories {

	val fireRing = AccessoryDefinition(
		key = Accessories.fireRing,
		slot = AccessorySlot.primary,
		cooldown = 8f,
		useRange = 15f,
		equippedFrame = EquipmentFrames.fireStaff,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.summonAtTarget,
				recipient = EffectRecipient.projectile,
				proficiencies = setOf(Proficiencies.rangedCombat, Proficiencies.magic),
				duration = 4f,
				spawnsScene = "res://entities/effect/FireRing.tscn",
			),
		)
	)

	val fireStaff = AccessoryDefinition(
		key = Accessories.fireStaff,
		slot = AccessorySlot.primary,
		cooldown = 1f,
		useRange = 15f,
		equippedFrame = EquipmentFrames.fireStaff,
		actionEffects = listOf(
			AccessoryEffect(
				type = AccessoryEffects.damage,
				recipient = EffectRecipient.projectile,
				proficiencies = setOf(Proficiencies.magic, Proficiencies.rangedCombat),
				damages = damagesOf(
					DamageTypes.fire to 20,
				),
				spawnsScene = "res://entities/effect/Fireball.tscn",
				speed = 20f,
			),
		)
	)

}

object BuffDefinitions {

	val burning = AccessoryDefinition(
		key = Accessories.burning,
		slot = AccessorySlot.passive,
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
		slot = AccessorySlot.passive,
		passiveEffects = listOf(
			newPassiveEffect(AccessoryEffects.invisible),
			removeOnUseAny,
		),
	)
}
