package compuquest.simulation.general

import compuquest.simulation.definition.Factions
import silentorb.mythic.ent.Id

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
}

data class Interactable(
  val target: Id,
  val action: String,
  val onInteract: String,
)

private const val interactableMaxDistance = 4f

fun getInteractable(world: World, actor: Id): Interactable? {
  val target = castCharacterRay(world, actor, interactableMaxDistance)
  return if (target != null) {
    val character = world.deck.characters[target]
    if (character != null && character.isForHire)
      Interactable(
        target = target,
        action = InteractionActions.talk,
        onInteract = InteractionBehaviors.jobInterview,
      )
    else
      null
  } else
    null
}
