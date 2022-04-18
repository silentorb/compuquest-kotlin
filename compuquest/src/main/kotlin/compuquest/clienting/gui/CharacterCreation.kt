package compuquest.clienting.gui

import compuquest.clienting.Client
import compuquest.simulation.characters.Accessory
import compuquest.simulation.characters.AccessoryDefinition
import compuquest.simulation.characters.CharacterDefinition
import compuquest.simulation.general.*
import compuquest.simulation.input.Commands
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.emptyId
import silentorb.mythic.happening.*

data class CharacterCreation(
	val type: Key? = null,
	val accessories: List<AccessoryDefinition>? = null,
)

const val setCharacterCreationAccessories = "setCharacterCreationAccessories"
const val setCharacterCreationProfession = "setCharacterCreationProfession"

fun navigateToEquipCharacterScreen(actor: Id) =
	Event(Commands.navigate, actor, Screens.equipCharacter)

fun getPlayerAddedEvents(events: Events): List<Id> =
	filterEventTargets(playerAddedEvent, events)

fun updateCharacterCreationStates(
	scenario: Scenario,
	deck: Deck,
	events: Events,
	characterCreationStates: Map<Id, CharacterCreation>
): Map<Id, CharacterCreation> =
	if (scenario.defaultPlayerProfession != null && !scenario.characterCustomization)
		mapOf()
	else {
		val additions = getPlayerAddedEvents(events)
			.associateWith {
				CharacterCreation(
					type = scenario.defaultPlayerProfession,
				)
			}

		val removals = characterCreationStates.keys.filter { deck.characters.containsKey(it) }

		val updates = (characterCreationStates - removals)
			.mapValues { (actor, creation) ->
				val actorEvents = events.filter {
					it.target == actor &&
							(it.type == setCharacterCreationAccessories || it.type == setCharacterCreationProfession)
				}

				if (actorEvents.any())
					creation.copy(
						type = creation.type
							?: firstEventByType<Key>(setCharacterCreationProfession, actorEvents)?.value,
						accessories = creation.accessories
							?: firstEventByType<List<AccessoryDefinition>>(setCharacterCreationAccessories, actorEvents)?.value,
					)
				else
					creation
			}

		updates + additions
	}

fun characterCreationNavigation(client: Client, scenario: Scenario, deck: Deck, actor: Id): Events =
	if (!deck.characters.containsKey(actor)) {
		val creationState = client.characterCreationStates[actor]
		when {
			scenario.defaultPlayerProfession == null && creationState?.type == null ->
				listOf(Event(Commands.navigate, actor, Screens.chooseProfession))

			scenario.characterCustomization && creationState?.accessories == null && scenario.characterCustomization ->
				listOf(navigateToEquipCharacterScreen(actor))

			else -> listOf(
				newEvent(
					newPlayerCharacterEvent, actor, NewPlayerCharacter(
						type = scenario.defaultPlayerProfession ?: creationState?.type!!,
						accessories = creationState?.accessories,
					)
				),
			)
		}
	} else
		listOf()

// This function is designed to only trigger changes on certain events.
// Otherwise difficult non-threading race conditions can occur.
fun characterCreationNavigation(client: Client, scenario: Scenario, deck: Deck, events: Events): Events =
	events.filter {
		(it.type == playerAddedEvent ||
				it.type == setCharacterCreationAccessories ||
				it.type == setCharacterCreationProfession)
				&& it.target is Id
	}
		.flatMap { event ->
			characterCreationNavigation(client, scenario, deck, event.target as Id)
		}
