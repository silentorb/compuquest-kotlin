package compuquest.simulation.updating

import compuquest.simulation.general.*
import silentorb.mythic.godoting.deleteNode
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventTargets
import silentorb.mythic.ent.Id
import silentorb.mythic.timing.expiredTimers

fun deleteEntities(events: Events, world: World): World {
	val deck = world.deck
	val deletions = filterEventTargets<Id>(deleteEntityCommand, events) +
			expiredTimers(deck.timers) +
			getConsumedAccessories(deck.accessories, events)

	val bodyDeletions = filterEventTargets<Id>(deleteBodyCommand, events)

	val distinctBodyDeletions = (deletions + bodyDeletions).distinct()
	for (deletion in distinctBodyDeletions) {
		val body = world.bodies[deletion]
		if (body != null) {
			deleteNode(body)
		}
	}
	val newDeck = removeEntities(deletions)(deck)

	return world.copy(
		bodies = world.bodies - distinctBodyDeletions,
		deck = newDeck,
	)
}
