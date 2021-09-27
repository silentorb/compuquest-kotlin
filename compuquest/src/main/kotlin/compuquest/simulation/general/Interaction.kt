package compuquest.simulation.general

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key

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
}

data class Interactable(
  val target: Id,
  val action: String,
  val onInteract: String,
)

private const val interactableMaxDistance = 4f

fun getOnInteract(deck: Deck, target: Id, targetCharacter: Character): Key? =
  when {
    targetCharacter.isForHire -> InteractionBehaviors.jobInterview
    getAvailableQuests(deck, target).any() -> InteractionBehaviors.offerQuests
    readyToCompleteQuests(deck, targetCharacter).any() -> InteractionBehaviors.completeQuest
    else -> null
  }

fun getInteractable(world: World, actor: Id): Interactable? {
  val target = castCharacterRay(world, actor, interactableMaxDistance)
  return if (target != null) {
    val character = world.deck.characters[target]
    if (character != null) {
      val onInteract = getOnInteract(world.deck, target, character)
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
  } else
    null
}
