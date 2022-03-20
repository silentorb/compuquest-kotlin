package compuquest.simulation.updating

import compuquest.definition.Accessories
import compuquest.simulation.characters.Accessory
import compuquest.simulation.characters.hasPassiveEffect
import compuquest.simulation.characters.updatePlayerRig
import compuquest.simulation.combat.applyDamageNodeEvents
import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import compuquest.simulation.general.applyNodeInteractions
import compuquest.simulation.input.PlayerInputs
import compuquest.simulation.input.emptyPlayerInput
import compuquest.simulation.physics.setLocationEvent
import godot.core.Color
import godot.core.Vector3
import scripts.entities.CharacterBody
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventsByType

val partialInvisibleColor = Color(1, 1, 1, 0.5)

fun updateCharacterBodyAccessoryInfluences(body: CharacterBody, accessories: Table<Accessory>) {
	val color = if (hasPassiveEffect(accessories, Accessories.invisible))
		partialInvisibleColor
	else
		Color.white

	body.sprite?.modulate = color
	body.equippedSprite?.modulate = color
}

fun syncBodyLocations(world: World) {
	for (body in world.deck.bodies.values) {
		if (body is CharacterBody) {
			body.location = body.globalTransform.origin
		}
	}
}

fun updateCharacterBody(deck: Deck, actor: Id, body: CharacterBody, inputs: PlayerInputs) {
	val character = deck.characters[actor]
	if (character != null) {
		val input = inputs[actor] ?: emptyPlayerInput
		body.update(input, character, simulationDelta)
	}
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
		val body = world.deck.bodies[actor] as? CharacterBody
		val character = deck.characters[actor]
		if (body != null && character != null) {
			body.isActive = character.isAlive
			val input = inputs[actor]
			if (input != null) {
				updatePlayerRig(world, actor, body, input)
			}
		}
	}

	for ((actor, body) in world.deck.bodies) {
		if (body is CharacterBody) {
			updateCharacterBody(deck, actor, body, inputs)
		}
		val moveEvent = moveEvents.firstOrNull { it.target == actor }
		if (moveEvent != null) {
			body.translation = moveEvent.value
		}
	}

	applyDamageNodeEvents(world, events)
	applyNodeInteractions(world, inputs)
}
