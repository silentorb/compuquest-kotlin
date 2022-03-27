package compuquest.definition

import compuquest.simulation.characters.CharacterDefinition
import compuquest.simulation.intellect.knowledge.Personality
import silentorb.mythic.ent.KeyTable

object CharacterDefinitions {

	val cleric = CharacterDefinition(
		key = Characters.cleric,
		depiction = "viking",
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
		depiction = "sprites",
		frame = 3,
		health = 30,
		accessories = listOf(
			Accessories.bite,
		),
		personality = Personality(
			roaming = true,
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
		depiction = "skeleton",
		health = 50,
		accessories = listOf(
			Accessories.fireStaff,
		),
	)

	val skeletonAssassin = CharacterDefinition(
		key = Characters.skeletonAssassin,
		depiction = "sprites",
		frame = 10,
		health = 40,
		accessories = listOf(
			Accessories.sai,
			Accessories.invisibility,
		),
	)

	val skeletonSage = CharacterDefinition(
		key = Characters.skeletonSage,
		depiction = "sprites",
		frame = 9,
		health = 50,
		accessories = listOf(
			Accessories.summonSquid,
		),
	)

	val viking = CharacterDefinition(
		key = Characters.viking,
		depiction = "viking",
		health = 50,
		accessories = listOf(
			Accessories.fireStaff,
		),
	)

	val squid = CharacterDefinition(
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
	)
}

object PlayerProfessionDefinitions {

	val playerCleric = CharacterDefinition(
		key = Characters.playerCleric,
		name = "Cleric",
		depiction = "deevee",
		health = 100,
		accessories = listOf(
			Accessories.heal,
			Accessories.summonSquid,
		),
	)

	val ninja = CharacterDefinition(
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
	)

	val playerViking = CharacterDefinition(
		key = Characters.playerViking,
		name = "Viking",
		depiction = "viking",
		health = 100,
		accessories = listOf(
			Accessories.rifle,
			Accessories.summonFox,
		),
	)

	val wizard = CharacterDefinition(
		key = Characters.wizard,
		name = "Wizard",
		depiction = "sprites",
		frame = 7,
		health = 100,
		accessories = listOf(
			Accessories.grenade,
			Accessories.summonIceWall,
		),
	)
}
