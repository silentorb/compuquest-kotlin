package compuquest.simulation.updating

import compuquest.simulation.characters.updatePlayerRig
import compuquest.simulation.general.*
import silentorb.mythic.happening.Events
import compuquest.simulation.happening.gatherEvents
import compuquest.simulation.input.PlayerInputs
import compuquest.simulation.input.emptyPlayerInput
import compuquest.simulation.intellect.updateSpirit
import scripts.entities.CharacterBody
import silentorb.mythic.ent.mapTable
import silentorb.mythic.godoting.tempCatchStatement

const val simulationFps: Int = 60
const val simulationDelta: Float = 1f / simulationFps.toFloat()

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

	for (actor in deck.players.keys) {
		val body = world.bodies[actor] as? CharacterBody
		val character = deck.characters[actor]
		if (body != null && character != null) {
			body.isActive = character.isAlive
			val input = inputs[actor]
			if (input != null) {
				updatePlayerRig(body, input)
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
	}
}

fun updateDepictions(previous: World?, next: World) {
	val characters = previous?.deck?.characters
	for (after in next.deck.characters) {
		val before = if (characters != null) characters[after.key] else null
		val animation = after.value.depiction
		val frame = after.value.frame
		if (before == null || before.depiction != animation || before.frame != frame) {
			val node = next.sprites[after.key]
			if (node != null) {
				tempCatchStatement {
					node.animation = animation
					node.frame = frame.toLong()
				}
			}
		}
	}
}

fun updateWorldDay(world: World): World =
	world.copy(
		day = updateDay(world.day),
		step = world.step + 1
	)

fun updateWorld(events: Events, inputs: PlayerInputs, delta: Float, worlds: List<World>): World {
	val world = updateWorldDay(worlds.last())

	val world2 = syncMythic(world)

	// Update the AI state before events are generated because AI state is not directly dependent on
	// events but AI state does generate events and it is better for the AI state and AI event generation
	// to use the same world state or bugs could arise such as the AI state referring to an entity that
	// was just deleted and the AI event generation trying to access that deleted entity.
	// That in turn removes the need for a lot of defensive code.
	val world3 = world2.copy(
		deck = world2.deck.copy(
			spirits = mapTable(world2.deck.spirits, updateSpirit(world)),
		)
	)

	val events2 = gatherEvents(world3, worlds.dropLast(1).lastOrNull(), delta, events)
	val deck = updateDeck(events2, world3, delta)
	val world4 = world3.copy(
		deck = deck,
	)
	updateDepictions(world, world4)
	val world5 = deleteEntities(events2, world4)
	val world6 = newEntities(events2, world5)
	syncGodot(world6, events2, inputs)
	return world6.copy(previousEvents = events2)
}
