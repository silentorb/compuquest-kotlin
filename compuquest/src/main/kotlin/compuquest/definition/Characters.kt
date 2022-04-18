package compuquest.definition

import compuquest.simulation.characters.CharacterDefinition
import compuquest.simulation.intellect.knowledge.Personality
import compuquest.simulation.intellect.knowledge.Roaming

object CharacterDefinitions {

	val cleric = CharacterDefinition(
		key = Characters.cleric,
		proficiencies = mapOf(
			Proficiencies.nature to 1,
		),
		depiction = "deevee",
		health = 40,
		accessories = listOf(
			Accessories.heal,
		),
	)

	val child = CharacterDefinition(
		key = Characters.child,
		depiction = "sprites",
		frame = 2,
		health = 30,
		accessories = listOf(
		),
	)

	val fox = CharacterDefinition(
		key = Characters.fox,
		proficiencies = mapOf(
			Proficiencies.closeCombat to 1,
			Proficiencies.nature to 1,
		),
		depiction = "sprites",
		frame = 3,
		health = 30,
		accessories = listOf(
			Accessories.bite,
		),
		personality = Personality(
			roaming = Roaming.roaming,
		)
	)

	val player = CharacterDefinition(
		key = Characters.player,
		depiction = "deevee",
		health = 100,
		accessories = listOf(
		),
	)

	val skeleton = CharacterDefinition(
		key = Characters.skeleton,
		proficiencies = mapOf(
			Proficiencies.rangedCombat to 1,
			Proficiencies.nature to 1,
		),
		depiction = "skeleton",
		health = 50,
		speed = 5f,
		accessories = listOf(
			Accessories.fireStaff,
		),
		personality = Personality(
			roaming = Roaming.roamWhenAlerted,
		),
		sounds = CommonCharacterSounds.skeleton,
	)

	val skeletonAssassin = CharacterDefinition(
		key = Characters.skeletonAssassin,
		speed = 8f,
		proficiencies = mapOf(
			Proficiencies.closeCombat to 1,
			Proficiencies.cunning to 1,
		),
		depiction = "sprites",
		frame = 10,
		health = 40,
		accessories = listOf(
			Accessories.sai,
			Accessories.invisibility,
		),
		personality = Personality(
			roaming = Roaming.roamWhenAlerted,
		),
		sounds = CommonCharacterSounds.skeleton,
	)

	val skeletonSage = CharacterDefinition(
		key = Characters.skeletonSage,
		proficiencies = mapOf(
			Proficiencies.magic to 1,
			Proficiencies.nature to 1,
		),
		depiction = "sprites",
		frame = 9,
		health = 50,
		accessories = listOf(
			Accessories.summonSquid,
		),
		sounds = CommonCharacterSounds.skeleton,
	)

	val viking = CharacterDefinition(
		key = Characters.viking,
		proficiencies = mapOf(
			Proficiencies.rangedCombat to 1,
		),
		depiction = "viking",
		health = 50,
		accessories = listOf(
			Accessories.fireStaff,
		),
	)

	val squid = CharacterDefinition(
		key = Characters.squid,
		proficiencies = mapOf(
			Proficiencies.nature to 1,
			Proficiencies.closeCombat to 1,
		),
		depiction = "sprites",
		frame = 5,
		health = 40,
		speed = 5f,
		accessories = listOf(
			Accessories.bite,
		),
		personality = Personality(
			roaming = Roaming.roaming,
		)
	)
}

object PlayerProfessionDefinitions {

	val cleric = CharacterDefinition(
		key = Characters.playerCleric,
		proficiencies = mapOf(
			Proficiencies.nature to 2,
		),
		name = "Cleric",
		depiction = "deevee",
		health = 100,
		accessories = listOf(
			Accessories.heal,
			Accessories.summonSquid,
		),
		sounds = CommonCharacterSounds.player,
	)

	val marine = CharacterDefinition(
		key = Characters.marine,
		name = "Marine",
		proficiencies = mapOf(
			Proficiencies.rangedCombat to 1,
			Proficiencies.mechanics to 1,
		),
		depiction = "medium",
		frame = 1,
		health = 120,
		accessories = listOf(
			Accessories.energyStaff,
			Accessories.grenade,
		),
		sounds = CommonCharacterSounds.player,
	)

	val ninja = CharacterDefinition(
		key = Characters.ninja,
		name = "Ninja",
		proficiencies = mapOf(
			Proficiencies.closeCombat to 1,
			Proficiencies.cunning to 1,
		),
		depiction = "sprites",
		frame = 8,
		health = 80,
		accessories = listOf(
			Accessories.backstab,
			Accessories.sai,
			Accessories.invisibility,
		),
		sounds = CommonCharacterSounds.player,
	)

	val viking = CharacterDefinition(
		key = Characters.playerViking,
		name = "Viking",
		proficiencies = mapOf(
			Proficiencies.closeCombat to 2,
		),
		depiction = "viking",
		health = 120,
		accessories = listOf(
			Accessories.sword,
			Accessories.summonFox,
		),
		sounds = CommonCharacterSounds.player,
	)

	val wizard = CharacterDefinition(
		key = Characters.wizard,
		name = "Wizard",
		proficiencies = mapOf(
			Proficiencies.magic to 2,
		),
		depiction = "sprites",
		frame = 7,
		health = 100,
		accessories = listOf(
			Accessories.mortar,
			Accessories.summonIceWall,
		),
		sounds = CommonCharacterSounds.player,
	)
}
