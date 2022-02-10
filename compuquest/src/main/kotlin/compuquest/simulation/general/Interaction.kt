package compuquest.simulation.general

import compuquest.simulation.characters.Character
import compuquest.simulation.input.PlayerInputs
import compuquest.simulation.physics.castRay
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.ent.emptyId
import silentorb.mythic.happening.Events

object InteractionActions {
	val close = "close"
	val open = "open"
	val openClose = "openClose"
	val sleep = "sleep"
	val read = "read"
	val take = "take"
	val talk = "talk"
}

object InteractionBehaviors {
	val close = "close"
	val give = "give"
	val harvest = "harvest"
	val open = "open"
	val openClose = "openClose"
	val sleep = "sleep"
	val devotion = "devotion"
	val take = "take"
	val jobInterview = "jobInterview"
	val offerQuests = "offerQuests"
	val completeQuest = "completeQuests"
	val talk = "talk"
}

data class Interactable(
	val target: Id?,
	val action: String, // The distinction between action and onInteract may eventually be deprecated
	val onInteract: Key,
	val targetNode: Interactive? = null,
)

fun newNodeInteractable(node: Interactive, action: String) =
	Interactable(target = null, action = action, onInteract = "", targetNode = node)

interface Interactive {
	fun getInteractable(world: World): Interactable?
	fun onInteraction(world: World, actor: Id)
}

private const val interactableMaxDistance = 4f

fun canGiveToCharacter(world: World, actor: Id, target: Id): Boolean {
	val character = world.deck.characters[actor]
	return character != null &&
			character.activeAccessory != emptyId
}

fun getInteractable(world: World, actor: Id): Interactable? {
	val collider = castRay(world, actor, interactableMaxDistance)
	val target = if (collider != null)
		getBodyEntityId(world, collider)
	else
		null

	return if (target != null) {
		val character = world.deck.characters[target]
		if (character != null) {
			val onInteract = when {
				canGiveToCharacter(world, actor, target) -> InteractionBehaviors.give
				character.attributes.contains("talk") -> InteractionBehaviors.talk
				else -> null
			}
			if (onInteract != null)
				Interactable(
					target = target,
					action = onInteract,
					onInteract = onInteract,
				)
			else
				null
		} else
			null
	} else if (collider is Interactive) {
		collider.getInteractable(world)
	} else
		null
}

fun getPlayerInteractionEvents(deck: Deck, actor: Id, player: Player): Events {
	val interaction = player.canInteractWith
	val accessory = deck.characters[actor]?.activeAccessory
	return if (interaction?.target != null && accessory != null)
		when (interaction.onInteract) {
			InteractionBehaviors.give -> {
				listOf(transferAccessory(accessory, interaction.target))
			}
			else -> listOf()
		}
	else
		listOf()
}

fun applyNodeInteractions(world: World, playerInputs: PlayerInputs) {
	val deck = world.deck
	for ((actor, player) in deck.players) {
		val node = player.canInteractWith?.targetNode
		if (node != null) {
			val input = playerInputs[actor]
			if (input?.interact == true) {
				node.onInteraction(world, actor)
			}
		}
	}
}
