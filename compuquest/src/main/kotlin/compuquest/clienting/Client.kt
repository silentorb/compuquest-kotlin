package compuquest.clienting

import compuquest.clienting.gui.MenuStack
import compuquest.clienting.gui.updateMenuStack
import compuquest.simulation.general.World
import compuquest.simulation.general.getPlayer
import compuquest.simulation.input.Commands
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.Events

data class PlayerGui(
  val menuStack: MenuStack = listOf(),
)

data class Client(
  val menuStack: MenuStack = listOf(),
  val options: AppOptions,
)

fun newClient() =
  Client(
    options = loadOptions(),
  )

fun restartClient(client: Client) =
  client.copy(
    menuStack = listOf(),
  )

fun getPlayerMenuStack(client: Client, player: Id): MenuStack =
  client.menuStack

fun updateClient(world: World?, events: Events, client: Client): Client {
  val player = getPlayer(world)
  return client.copy(
    menuStack = if (player != null)
      updateMenuStack(player.value)(events, client.menuStack)
    else
      client.menuStack,
  )
}

fun eventsFromClient(player: Id, client: Client, previous: Client?): Events =
  if (previous == null)
    listOf()
  else
    listOfNotNull(
      if (client.menuStack.none() and previous.menuStack.any())
        Event(Commands.finishInteraction, player)
      else
        null
    )
