package compuquest.clienting

import compuquest.clienting.gui.MenuStack
import silentorb.mythic.ent.Id

data class PlayerGui(
  val menuStack: MenuStack = listOf(),
)

data class Client(
  var menuStack: MenuStack = listOf(),
//  val playerGuiMap: Map<Id, PlayerGui> = mapOf(),
)

fun getPlayerMenuStack(client: Client, player: Id): MenuStack =
  client.menuStack

fun setPlayerMenuStack(client: Client, player: Id, stack: MenuStack) {
  client.menuStack = stack
}
