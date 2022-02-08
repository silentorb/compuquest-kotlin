package compuquest.population

import compuquest.simulation.general.World
import compuquest.simulation.intellect.newSpirit
import compuquest.simulation.updating.newEntitiesFromHands
import godot.Node
import scripts.entities.AttachCharacter
import scripts.entities.PlayerSpawner
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.godoting.findChildrenOfType

fun processSceneEntities(scene: Node, world: World): World {
	val definitions = world.definitions
	val nextId = world.nextId.source()
	val attachments = findChildrenOfType<AttachCharacter>(scene)
	val playerSpawners = findChildrenOfType<PlayerSpawner>(scene)

	val hands = if (getDebugBoolean("NO_MONSTERS"))
		listOf()
	else
		attachments
			.flatMap { attachment ->
				addCharacter(
					definitions,
					nextId(),
					nextId,
					attachment,
					listOf(newSpirit())
				)
			}

	val world2 = newEntitiesFromHands(hands, world)
		.copy(
			playerSpawners = playerSpawners,
		)

	return populateQuests(world2)
}
