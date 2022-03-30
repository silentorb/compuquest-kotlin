package compuquest.simulation.characters

import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import compuquest.simulation.general.deleteEntityCommand
import compuquest.simulation.happening.UseAction
import compuquest.simulation.happening.useActionEvent
import compuquest.simulation.updating.simulationDelta
import compuquest.simulation.updating.updateCharacterBodyAccessoryInfluences
import scripts.entities.CharacterBody
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

fun updateContainer(world: World, events: Events): (Id, AccessoryContainer) -> AccessoryContainer =
	{ actor, container ->
		val deck = world.deck
		val actorEvents = filterEventsByTarget(actor, events)
		val newAccessories = filterEventValues<Accessory>(addNewAccessoryToContainerEvent, actorEvents)
			.associateBy { world.nextId.source()() }

		val updated = if (container.effectsMode == ContainerEffectsMode.usesEffects)
			container.accessories.mapValues(mapEntry(updateOwnedAccessory(container, events, simulationDelta)))
		else
			container.accessories

		val deleted = events
			.filter {
				(
						it.type == deleteEntityCommand
								&& container.accessories.containsKey(it.target)
						)
						|| (
						it.type == useActionEvent
								&& it.value is UseAction
								&& container.accessories[it.value.action]?.definition?.isConsumable == true
						)
			}
			.map { (it.value as? UseAction)?.action ?: it.target as Id } +
				updated.filter { it.value.duration == 0 }.keys

		val remaining = (updated - deleted)

		val accessories = remaining + newAccessories
		if (newAccessories.any() || deleted.any()) {
			val body = deck.bodies[actor] as? CharacterBody
			if (body != null) {
				updateCharacterBodyAccessoryInfluences(deck, actor, body, accessories)
			}
		}

		container.copy(
			accessories = accessories,
		)
	}
