package compuquest.simulation.general

import compuquest.simulation.input.Commands
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.handleEvents
import kotlin.math.max

const val maxPartySize = 4
const val playerFaction = "player"

data class Player(
  val faction: Key,
  val party: List<Id> = listOf(),
  val canInteractWith: Interactable? = null,
  val interactingWith: Id? = null,
)

fun updateInteractingWith(player: Player) = handleEvents<Id?> { event, value ->
  when (event.type) {
    Commands.interact -> player.canInteractWith?.target
    Commands.finishInteraction -> null
    else -> value
  }
}

fun updateParty() = handleEvents<List<Id>> { event, value ->
  when (event.type) {
    Commands.hiredNpc -> value + event.value as Id
    else -> value
  }
}

fun shouldRefreshPlayerSlowdown(player: Player, events: Events): Boolean =
  events.any { it.type == detrimentalEffectCommand && player.party.contains(it.target)}

fun updatePlayer(world: World, events: Events, delta: Float): (Id, Player) -> Player = { actor, player ->
  val deck = world.deck
  val canInteractWith = if (player.interactingWith == null)
    getInteractable(world, actor)
  else
    null

  val removedCharacters = events.filter { it.type == removeFactionMemberEvent }
    .mapNotNull {
     if (deck.characters[it.target]?.faction == player.faction)
       it.target as? Id
      else
        null
    }

  val playerEvents = events.filter { it.target == actor }
  val interactingWith = updateInteractingWith(player)(playerEvents, player.interactingWith)
  val party = updateParty()(playerEvents, player.party) - removedCharacters
  player.copy(
    canInteractWith = canInteractWith,
    interactingWith = interactingWith,
    party = party,
  )
}
