package compuquest.definition

import compuquest.simulation.characters.CharacterDefinition
import compuquest.simulation.intellect.knowledge.Personality
import silentorb.mythic.ent.KeyTable

val playerProfessionDefinitions: KeyTable<CharacterDefinition> = listOf(
	CharacterDefinition(
		key = Characters.playerCleric,
		name = "Cleric",
		depiction = "deevee",
		health = 100,
		accessories = listOf(
			Accessories.heal,
			Accessories.summonSquid,
		),
	),
	CharacterDefinition(
		key = Characters.ninja,
		name = "Ninja",
		depiction = "sprites",
		frame = 8,
		health = 100,
		accessories = listOf(
			Accessories.backstab,
			Accessories.sai,
			Accessories.invisibility,
		),
	),
	CharacterDefinition(
		key = Characters.playerViking,
		name = "Viking",
		depiction = "viking",
		health = 100,
		accessories = listOf(
			Accessories.rifle,
			Accessories.summonFox,
		),
	),
	CharacterDefinition(
		key = Characters.wizard,
		name = "Wizard",
		depiction = "sprites",
		frame = 7,
		health = 100,
		accessories = listOf(
			Accessories.banana,
			Accessories.summonIceWall,
		),
	),
).associateBy { it.key }

fun characterDefinitions(): KeyTable<CharacterDefinition> = listOf(
	CharacterDefinition(
		key = Characters.cleric,
		depiction = "viking",
		health = 40,
		accessories = listOf(
			Accessories.heal,
		),
	),
	CharacterDefinition(
		key = Characters.child,
		depiction = "sprites",
		frame = 2,
		health = 30,
		accessories = listOf(
		),
	),
	CharacterDefinition(
		key = Characters.fox,
		depiction = "sprites",
		frame = 3,
		health = 30,
		accessories = listOf(
			Accessories.bite,
		),
		personality = Personality(
			roaming = true,
		)
	),
	CharacterDefinition(
		key = Characters.player,
		depiction = "deevee",
		health = 100,
		accessories = listOf(
		),
	),
	CharacterDefinition(
		key = Characters.skeleton,
		depiction = "skeleton",
		health = 50,
		accessories = listOf(
			Accessories.fireStaff,
		),
	),
	CharacterDefinition(
		key = Characters.skeletonAssassin,
		depiction = "sprites",
		frame = 10,
		health = 40,
		accessories = listOf(
			Accessories.sai,
			Accessories.invisibility,
		),
	),
	CharacterDefinition(
		key = Characters.skeletonSage,
		depiction = "sprites",
		frame = 9,
		health = 50,
		accessories = listOf(
			Accessories.summonSquid,
		),
	),
	CharacterDefinition(
		key = Characters.viking,
		depiction = "viking",
		health = 50,
		accessories = listOf(
			Accessories.fireStaff,
		),
	),
	CharacterDefinition(
		key = Characters.squid,
		depiction = "sprites",
		frame = 5,
		health = 50,
		speed = 5f,
		accessories = listOf(
			Accessories.bite,
		),
		personality = Personality(
			roaming = true,
		)
	),
).associateBy { it.key }

val staticCharacterDefinitions = playerProfessionDefinitions + characterDefinitions()
