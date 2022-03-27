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

fun actionDefinitions() =
	reflectProperties<AccessoryDefinition>(AccessoryDefinitions)
		.associateBy { it.key }

fun buffDefinitions() =
	reflectProperties<AccessoryDefinition>(BuffDefinitions)
		.associateBy { it.key }

fun accessoryDefinitions(): KeyTable<AccessoryDefinition> =
	actionDefinitions() + buffDefinitions()

fun newDefinitions() =
	Definitions(
		accessories = accessoryDefinitions(),
		characters = staticCharacterDefinitions,
	)
