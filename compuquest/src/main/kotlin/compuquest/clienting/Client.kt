package compuquest.clienting

import compuquest.clienting.gui.MenuStack
import compuquest.clienting.gui.updateMenuStack
import compuquest.simulation.general.World
import compuquest.simulation.general.getPlayer
import silentorb.mythic.ent.Id
import silentorb.mythic.happening.Events

data class PlayerGui(
  val menuStack: MenuStack = listOf(),
)

data class Client(
  val menuStack: MenuStack = listOf(),
//  val playerGuiMap: Map<Id, PlayerGui> = mapOf(),
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
