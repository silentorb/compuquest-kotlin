package compuquest.simulation.updating

import compuquest.simulation.general.World
import compuquest.simulation.general.deleteBodyCommand
import compuquest.simulation.general.deleteEntityCommand
import compuquest.simulation.general.removeEntities
import silentorb.mythic.audio.finishedSounds
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.deleteNode
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventTargets
import silentorb.mythic.timing.expiredTimers

fun deleteEntities(events: Events, world: World): World {
	val deck = world.deck
	val deletions = filterEventTargets<Id>(deleteEntityCommand, events) +
			expiredTimers(deck.timers) +
			finishedSounds(world.definitions.soundDurations)(deck.sounds)


	val bodyDeletions = filterEventTargets<Id>(deleteBodyCommand, events)

	val distinctBodyDeletions = (deletions + bodyDeletions).distinct()
	for (deletion in distinctBodyDeletions) {
		val body = world.deck.bodies[deletion]
		if (body != null) {
			deleteNode(body, world.scene)
		}
	}
	val newDeck = removeEntities(deletions)(deck)

	return world.copy(
		deck = newDeck,
	)
}
