package compuquest.definition

import compuquest.simulation.characters.AccessoryDefinition
import compuquest.simulation.characters.CharacterDefinition
import compuquest.simulation.definition.Definitions
import silentorb.mythic.ent.KeyTable
import silentorb.mythic.ent.reflectProperties

val playerProfessionDefinitions =
	reflectProperties<CharacterDefinition>(PlayerProfessionDefinitions)
		.associateBy { it.key }

fun characterDefinitions() =
	reflectProperties<CharacterDefinition>(CharacterDefinitions)
		.associateBy { it.key }

val staticCharacterDefinitions = playerProfessionDefinitions + characterDefinitions()

fun getBuffDefinitions() =
	reflectAccessoryDefinitions(BuffDefinitions)

fun getAiOnlyDefinitions() =
	reflectAccessoryDefinitions(AiOnlyAccessories)

fun reflectAccessoryDefinitions(definitions: Any) =
	reflectProperties<AccessoryDefinition>(definitions)
		.associateBy { it.key }

fun accessoryDefinitions(): KeyTable<AccessoryDefinition> =
	reflectAccessoryDefinitions(AccessoryDefinitions) +
			getAiOnlyDefinitions() +
			reflectAccessoryDefinitions(PassiveDefinitions) +
			getBuffDefinitions()

fun newDefinitions() =
	Definitions(
		accessories = accessoryDefinitions(),
		characters = staticCharacterDefinitions,
	)
