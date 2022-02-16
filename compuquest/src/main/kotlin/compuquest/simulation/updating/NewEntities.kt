package compuquest.simulation.updating

import compuquest.simulation.general.*
import compuquest.simulation.input.Commands
import godot.AnimatedSprite3D
import godot.Spatial
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.Table
import silentorb.mythic.godoting.tempCatch
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues

inline fun <reified T> extractComponentsRaw(hands: List<Hand>): List<Pair<Id, T>> =
	hands
		.flatMap { hand ->
			if (hand.id != null)
				hand.components
					.filterIsInstance<T>()
					.map { hand.id to it }
			else
				listOf()
		}

inline fun <reified T> extractComponents(hands: List<Hand>): Table<T> =
	extractComponentsRaw<T>(hands)
		.associate { it }

fun newEntitiesFromHands(hands: List<Hand>, world: World): World {
	val nextId = world.nextId.source()
	val deck = world.deck
	val idHands = hands.map { hand ->
		if (hand.id == null)
			hand.copy(id = nextId())
		else
			hand
	}

	val nodes = extractComponentsRaw<Spatial>(idHands)
	val (spritesList, bodiesList) = nodes.partition { it.second is AnimatedSprite3D }
	val sprites = spritesList.associate { it } as Table<AnimatedSprite3D>
	val bodies = bodiesList.associate { it }

	tempCatch {
		for (body in bodies.values) {
			if (body.getParent() == null)
				world.scene.addChild(body)
		}
	}

	return world.copy(
		sprites = world.sprites + sprites,
		deck = allHandsToDeck(idHands, deck),
	)
}

fun newEntities(events: Events, world: World): World {
	val hands = filterEventValues<Hand>(newHandCommand, events)
	return newEntitiesFromHands(hands, world)
}
