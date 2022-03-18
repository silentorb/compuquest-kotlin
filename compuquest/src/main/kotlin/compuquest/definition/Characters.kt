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
		),
	),
	CharacterDefinition(
		key = Characters.playerNinja,
		name = "Ninja",
		depiction = "sprites",
		frame = 8,
		health = 100,
		accessories = listOf(
		),
	),
	CharacterDefinition(
		key = Characters.playerViking,
		name = "Viking",
		depiction = "viking",
		health = 100,
		accessories = listOf(
		),
	),
	CharacterDefinition(
		key = Characters.playerWizard,
		name = "Wizard",
		depiction = "sprites",
		frame = 7,
		health = 100,
		accessories = listOf(
		),
	),
).associateBy { it.key }

fun characterDefinitions(): KeyTable<CharacterDefinition> = mapOf(
	Characters.player to CharacterDefinition(
		key = Characters.player,
		depiction = "deevee",
		health = 100,
		accessories = listOf(
		),
	),
	Characters.skeleton to CharacterDefinition(
		key = Characters.skeleton,
		depiction = "skeleton",
		health = 50,
		accessories = listOf(
			Accessories.fireStaff,
		),
	),
	Characters.skeletonSage to CharacterDefinition(
		key = Characters.skeletonSage,
		depiction = "skeleton",
		health = 50,
		accessories = listOf(
			Accessories.summonSquid,
		),
	),
	Characters.viking to CharacterDefinition(
		key = Characters.viking,
		depiction = "viking",
		health = 50,
		accessories = listOf(
			Accessories.fireStaff,
		),
	),
	Characters.cleric to CharacterDefinition(
		key = Characters.cleric,
		depiction = "viking",
		health = 40,
		accessories = listOf(
			Accessories.heal,
		),
	),
	Characters.child to CharacterDefinition(
		key = Characters.child,
		depiction = "sprites",
		frame = 2,
		health = 30,
		accessories = listOf(
		),
	),
	Characters.fox to CharacterDefinition(
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
	Characters.squid to CharacterDefinition(
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
)

val staticCharacterDefinitions = playerProfessionDefinitions + characterDefinitions()
