package compuquest.definition

import compuquest.simulation.characters.CharacterDefinition
import compuquest.simulation.intellect.knowledge.Personality
import silentorb.mythic.ent.KeyTable

fun characterDefinitions(): KeyTable<CharacterDefinition> = mapOf(
	Characters.player to CharacterDefinition(
		name = "Player",
		depiction = "deevee",
		health = 100,
		accessories = listOf(
		),
	),
	Characters.ninja to CharacterDefinition(
		name = "Ninja",
		depiction = "deevee",
		health = 100,
		accessories = listOf(
		),
	),
	Characters.skeleton to CharacterDefinition(
		name = Characters.skeleton,
		depiction = "skeleton",
		health = 50,
		accessories = listOf(
			Accessories.fireStaff,
		),
	),
	Characters.skeletonSage to CharacterDefinition(
		name = Characters.skeletonSage,
		depiction = "skeleton",
		health = 50,
		accessories = listOf(
			Accessories.summonSquid,
		),
	),
	Characters.viking to CharacterDefinition(
		name = Characters.viking,
		depiction = "viking",
		health = 50,
		accessories = listOf(
			Accessories.fireStaff,
		),
	),
	Characters.cleric to CharacterDefinition(
		name = Characters.cleric,
		depiction = "viking",
		health = 40,
		accessories = listOf(
			Accessories.heal,
		),
	),
	Characters.child to CharacterDefinition(
		name = Characters.child,
		depiction = "sprites",
		frame = 2,
		health = 30,
		accessories = listOf(
		),
	),
	Characters.fox to CharacterDefinition(
		name = Characters.fox,
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
		name = Characters.squid,
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

val staticCharacterDefinitions = characterDefinitions()
