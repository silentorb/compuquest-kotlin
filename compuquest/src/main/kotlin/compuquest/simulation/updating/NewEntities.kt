package compuquest.simulation.updating

import compuquest.simulation.general.*
import godot.Node
import godot.Spatial
import silentorb.mythic.ent.Id
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

fun attachBodiesToScene(scene: Node, bodies: Table<Spatial>) {
	tempCatch {
		for (body in bodies.values) {
			if (body.getParent() == null)
				scene.addChild(body)
		}
	}
}

fun newEntitiesFromHands(hands: List<Hand>, world: World): World {
	val nextId = world.nextId.source()
	val deck = world.deck
	val idHands = hands.map { hand ->
		if (hand.id == null)
			hand.copy(id = nextId())
		else
			hand
	}

	val spatials = extractComponentsRaw<Spatial>(idHands)
	attachBodiesToScene(world.scene, spatials.associate { it })

	return world.copy(
		deck = allHandsToDeck(idHands, deck),
	)
}

fun newEntities(events: Events, world: World): World {
	val hands = filterEventValues<Hand>(newHandCommand, events)
	return newEntitiesFromHands(hands, world)
}
