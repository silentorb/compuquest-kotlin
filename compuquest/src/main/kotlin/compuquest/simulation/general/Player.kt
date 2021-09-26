package compuquest.simulation.general

import compuquest.simulation.input.Commands
import scripts.gui.ManagementScreens
import scripts.gui.gameOverScreen
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.happening.Events
import silentorb.mythic.happening.handleEvents

const val maxPartySize = 4
const val playerFaction = "player"

val hiredNpc = "hiredNpc"
val joinedPlayer = "joinedPlayer"
val addMemberToParty = "addMemberToParty"
val removeMemberFromParty = "removeMemberFromParty"

data class Player(
  val faction: Key,
  val party: List<Id> = listOf(),
  val canInteractWith: Interactable? = null,
  val interactingWith: Id? = null,
  val menu: String? = null,
  val isPlaying: Boolean = true,
)

fun updateInteractingWith(player: Player) = handleEvents<Id?> { event, value ->
  when (event.type) {
    Commands.interact -> player.canInteractWith?.target
    Commands.menuBack -> null
    else -> value
  }
}

val updateManagementMenu = handleEvents<String?> { event, value ->
  when (event.type) {
    Commands.manageMembers -> ManagementScreens.members.name
    Commands.manageQuests -> ManagementScreens.quests.name
    Commands.menuBack -> null
    else -> value
  }
}

fun updateParty() = handleEvents<List<Id>> { event, value ->
  when (event.type) {
    hiredNpc -> value + event.value as Id
    addMemberToParty -> {
      val member = event.value as? Id
      if (member != null && value.size < maxPartySize)
        value + member
      else
        value
    }
    removeMemberFromParty -> {
      val member = event.value as? Id
      if (member != null && value.size > 1)
        value - member
      else
        value
    }
    else -> value
  }
}

fun shouldRefreshPlayerSlowdown(player: Player, events: Events): Boolean =
  events.any { it.type == detrimentalEffectCommand && player.party.contains(it.target) }

fun updatePlayer(world: World, events: Events, delta: Float): (Id, Player) -> Player = { actor, player ->
  val deck = world.deck
  val canInteractWith = if (player.interactingWith == null && player.isPlaying)
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

  val isPlaying = party.any { deck.characters[it]?.isAlive ?: false }

  val menu = if (!isPlaying && player.isPlaying)
    gameOverScreen
  else
    updateManagementMenu(events, player.menu)

  player.copy(
    canInteractWith = canInteractWith,
    interactingWith = interactingWith,
    menu = menu,
    party = party,
    isPlaying = isPlaying,
  )
}

fun getNonPartyMembers(deck: Deck, player: Player): Set<Id> =
  deck.characters
    .filterValues { it.faction == player.faction }
    .keys - player.party
