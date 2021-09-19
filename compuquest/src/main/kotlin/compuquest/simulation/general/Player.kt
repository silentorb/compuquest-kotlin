package compuquest.simulation.general

import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key

const val maxPartySize = 4
const val playerFaction = "player"

data class Player(
  val faction: Key,
  val party: List<Id> = listOf(),
  val canInteractWith: Interactable? = null,
  val interactingWith: Id? = null,
)

fun updatePlayer(world: World): (Id, Player) -> Player = { actor, player ->
  val deck = world.deck
  val canInteractWith = if (player.interactingWith == null)
    getInteractable(world, actor)
  else
    player.canInteractWith

//  val interactingWith = if (player.interactingWith == null && deck.players.containsKey(actor))
//    updateInteracting(world, actor)
//  else
//    player.interactingWith
  player.copy(
    canInteractWith = canInteractWith,
  )
}
