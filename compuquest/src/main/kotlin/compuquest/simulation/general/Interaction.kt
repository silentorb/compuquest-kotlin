package compuquest.simulation.general

import compuquest.simulation.combat.DamageNodeInfo
import compuquest.simulation.combat.damageNodeEvent
import compuquest.simulation.input.Commands
import compuquest.simulation.physics.castCharacterRay
import compuquest.simulation.physics.castRay
import godot.Node
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.filterEventValues

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
	val action: String,
	val onInteract: Key,
	val targetNode: Interactive? = null,
)

fun newNodeInteractable(node: Interactive) =
	Interactable(target = null, action = "", onInteract = "", targetNode = node)

interface Interactive {
	fun getInteractable(world: World): Interactable?
	fun onInteraction(world: World, actor: Id)
}

private const val interactableMaxDistance = 4f

fun getInteractable(world: World, actor: Id): Interactable? {
	val collider = castRay(world, actor, interactableMaxDistance)
	val target = if (collider != null)
		getBodyEntityId(world, collider)
	else
		null

	return if (target != null) {
		val character = world.deck.characters[target]
		if (character != null) {
			val onInteract = if (character.attributes.contains("talk"))
				InteractionBehaviors.talk
			else
				null
//        getOnInteracts(world.deck, target, character)
			if (onInteract != null)
				Interactable(
					target = target,
					action = InteractionActions.talk,
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

fun applyNodeInteractions(world: World, events: Events) {
	val deck = world.deck
	for ((actor, player) in deck.players) {
		val node = player.canInteractWith?.targetNode
		if (node != null) {
			if (events.any { it.target == actor && it.type == Commands.interact }) {
				node.onInteraction(world, actor)
			}
		}
	}
}
