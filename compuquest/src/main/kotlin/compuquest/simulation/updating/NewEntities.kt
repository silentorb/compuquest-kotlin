package compuquest.simulation.updating

import compuquest.simulation.general.Hand
import compuquest.simulation.general.World
import compuquest.simulation.general.allHandsToDeck
import compuquest.simulation.general.newHandCommand
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

fun attachBodiesToScene(scene: Node, bodies: Collection<Spatial>) {
	tempCatch {
		for (body in bodies) {
			if (body.getParent() == null)
				scene.addChild(body)
		}
	}
}

fun newEntitiesFromHands(hands: List<Hand>, world: World): World {
	val nextId = world.nextId.source()
	val deck = allHandsToDeck(nextId, hands, world.deck)
	attachBodiesToScene(world.scene, deck.bodies.values)

	return world.copy(
		deck = deck,
	)
}

fun newEntities(events: Events, world: World): World {
	val hands = filterEventValues<Hand>(newHandCommand, events)
	return newEntitiesFromHands(hands, world)
}
