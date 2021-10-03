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
  val resurrect = "resurrect"
}

data class Interactable(
  val target: Id,
  val action: String,
  val onInteracts: List<String>,
)

private const val interactableMaxDistance = 4f

fun getOnInteracts(deck: Deck, target: Id, targetCharacter: Character): List<Key> =
  listOfNotNull(
    if (targetCharacter.attributes.contains("forHire")) InteractionBehaviors.jobInterview else null,
    if (getAvailableQuests(deck, target).any()) InteractionBehaviors.offerQuests else null,
    if (readyToCompleteQuests(deck, targetCharacter).any()) InteractionBehaviors.completeQuest else null,
    if (hasAccessoryWithEffect(deck.accessories, target, AccessoryEffects.resurrect)) InteractionBehaviors.completeQuest else null,
  )

fun getInteractable(world: World, actor: Id): Interactable? {
  val target = castCharacterRay(world, actor, interactableMaxDistance)
  return if (target != null) {
    val character = world.deck.characters[target]
    if (character != null) {
      val onInteracts = getOnInteracts(world.deck, target, character)
      if (onInteracts.any())
        Interactable(
          target = target,
          action = InteractionActions.talk,
          onInteracts = onInteracts,
        )
      else
        null
    } else
      null
  } else
    null
}
