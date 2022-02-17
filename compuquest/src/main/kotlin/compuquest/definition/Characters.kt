package compuquest.definition

import compuquest.simulation.characters.CharacterDefinition
import compuquest.simulation.definition.Factions
import compuquest.simulation.intellect.knowledge.Personality
import silentorb.mythic.ent.KeyTable

object Characters {
	const val child = "child"
	const val fox = "fox"
	const val player = "player"
	const val skeleton = "skeleton"
	const val viking = "viking"
}

fun characterDefinitions(): KeyTable<CharacterDefinition> = mapOf(
	Characters.player to CharacterDefinition(
		name = "Player",
		depiction = "deevee",
		corpseDecay = 0f,
		health = 100,
		accessories = listOf(
//			Accessories.rifle,
//			Accessories.berries,
		),
	),
	Characters.skeleton to CharacterDefinition(
		name = Characters.skeleton,
		depiction = "skeleton",
		health = 50,
		accessories = listOf(
			Accessories.rocketLauncher,
		),
	),
	Characters.viking to CharacterDefinition(
		name = Characters.viking,
		depiction = "viking",
		health = 50,
		accessories = listOf(
			Accessories.rocketLauncher,
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
)

val staticCharacterDefinitions = characterDefinitions()
