package compuquest.simulation.updating

import compuquest.simulation.characters.updatePlayerRig
import compuquest.simulation.general.World
import compuquest.simulation.input.PlayerInputs
import compuquest.simulation.input.emptyPlayerInput
import compuquest.simulation.physics.Body
import compuquest.simulation.physics.setLocationEvent
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventsByType

fun syncMythic(world: World): World {
	val deck = world.deck
	val bodies = world.bodies.mapValues { spatial ->
		Body(
			translation = spatial.value.globalTransform.origin,
			rotation = spatial.value.rotation,
		)
	}
	return world.copy(
		deck = deck.copy(
			bodies = bodies,
		)
	)
}

fun syncGodot(world: World, events: Events, inputs: PlayerInputs) {
	val deck = world.deck
//	val player = getPlayer(world)
//	if (player != null) {
////			player.value.interactingWith == null &&
////					Global.getMenuStack().none()
////					&& player.value.isPlaying
////      if (shouldRefreshPlayerSlowdown(player.key, events)) {
////        body.isSlowed = true
////      }
//		}
//	}

	val moveEvents = filterEventsByType<Vector3>(setLocationEvent, events)

	for (actor in deck.players.keys) {
		val body = world.bodies[actor] as? CharacterBody
		val character = deck.characters[actor]
		if (body != null && character != null) {
			body.isActive = character.isAlive
			val input = inputs[actor]
			if (input != null) {
				updatePlayerRig(world, actor, body, input)
			}
		}
	}

	for ((actor, body) in world.bodies) {
		if (body is CharacterBody) {
			val character = deck.characters[actor]
			if (character != null) {
				val input = inputs[actor] ?: emptyPlayerInput
				body.update(input, character, simulationDelta)
			}
		}
		val moveEvent = moveEvents.firstOrNull { it.target == actor }
		if (moveEvent != null) {
			body.translation = moveEvent.value
		}
	}
}
