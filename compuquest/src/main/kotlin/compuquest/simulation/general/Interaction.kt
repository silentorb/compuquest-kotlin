package compuquest.simulation.general

import compuquest.simulation.physics.castCharacterRay
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
  val talk = "talk"
}

data class Interactable(
  val target: Id,
  val action: String,
  val onInteract: Key,
)

private const val interactableMaxDistance = 4f

fun getInteractable(world: World, actor: Id): Interactable? {
  val target = castCharacterRay(world, actor, interactableMaxDistance)
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
  } else
    null
}
