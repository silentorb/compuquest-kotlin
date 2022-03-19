package compuquest.simulation.characters

import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import compuquest.simulation.general.deleteEntityCommand
import compuquest.simulation.updating.simulationDelta
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Table
import silentorb.mythic.ent.mapEntry
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues
import silentorb.mythic.happening.filterEventsByTarget

enum class ContainerEffectsMode {
	suppressEffects,
	usesEffects,
}

data class AccessoryContainer(
	val accessories: Table<Accessory> = mapOf(),
	val effectsMode: ContainerEffectsMode = ContainerEffectsMode.usesEffects,
)

const val addNewAccessoryToContainerEvent = "addNewAccessoryToContainer"

fun newAccessoryForContainer(container: Id, accessory: Accessory): Event =
	Event(addNewAccessoryToContainerEvent, container, accessory)

fun getOwnerAccessories(deck: Deck, owner: Id): Table<Accessory> =
	deck.containers[owner]?.accessories ?: mapOf()

fun getOwnerAccessory(deck: Deck, owner: Id, accessory: Id): Accessory? =
	deck.containers[owner]?.accessories?.getOrDefault(accessory, null)

fun updateContainers(world: World, events: Events): (Id, AccessoryContainer) -> AccessoryContainer =
	{ actor, container ->
		val actorEvents = filterEventsByTarget(actor, events)
		val newAccessories = filterEventValues<Accessory>(addNewAccessoryToContainerEvent, actorEvents)
			.associateBy { world.nextId.source()() }

		val deleted = events
			.filter { it.type == deleteEntityCommand && container.accessories.containsKey(it.target) }
			.map { it.target as Id }

		val remaining = (container.accessories - deleted)

		val updated = if (container.effectsMode == ContainerEffectsMode.usesEffects)
			remaining
				.mapValues(mapEntry(updateOwnedAccessory(container, events, simulationDelta)))
				.filter { it.value.duration != 0 }
		else
			remaining

		container.copy(
			accessories = updated + newAccessories
		)
	}
