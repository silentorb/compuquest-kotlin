package compuquest.population

import compuquest.simulation.characters.addCharacter
import compuquest.simulation.characters.newCharacterAccessories
import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.Factions
import compuquest.simulation.definition.ResourceType
import compuquest.simulation.general.*
import godot.Node
import godot.Resource
import godot.core.Vector3
import scripts.entities.AttachCharacter
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.NextId
import silentorb.mythic.godoting.*

fun parseFaction(faction: Key?, node: Node): Key {
	val rawFaction = faction ?: getString(node, "faction")
	return if (rawFaction == "")
		Factions.neutral
	else
		rawFaction
}

fun addQuests(nextId: NextId, client: Id, creature: Resource): Hands =
	getList<Resource>(creature, "quests")
		.map { quest ->
			Hand(
				id = nextId(),
				components = listOf(
					Quest(
						client = client,
						name = getString(quest, "name"),
						type = getString(quest, "type"),
						reward = mapOf(ResourceType.gold to getInt(quest, "rewardGold")),
						recipientName = getNonEmptyString(quest, "recipient"),
						duration = getInt(quest, "duration"),
						penaltyValue = getInt(quest, "penaltyValue"),
					)
				)
			)
		}

fun addExistingCharacter(
	definitions: Definitions,
	id: Id,
	nextId: NextId,
	node: AttachCharacter,
	additional: List<Any> = listOf()
): Hands {
	return tempCatch {
		val parent = node.getParent()
		val type = node.type
		val definition = definitions.characters[type] ?: throw Error("Unknown character type: $type")
		val characterBody = parent as CharacterBody
		addCharacter(definitions, definition, id, nextId, characterBody, relationships = listOf(), additional = additional)
	}
}
