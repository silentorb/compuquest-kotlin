package scripts.gui

import compuquest.simulation.general.Player
import compuquest.simulation.general.getPlayer
import compuquest.simulation.general.hiredNpc
import compuquest.simulation.general.joinedPlayer
import godot.Node
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Event

var lastMenu: String? = null

fun launchMenu(slot: Node, menu: Node) {
  clearChildren(slot)
  slot.addChild(menu)
}

fun launchMenu(slot: Node, scenePath: String): Node? {
  val menu = instantiateScene<Node>(scenePath)
  return if (menu != null) {
    launchMenu(slot, menu)
    menu
  } else
    null
}

fun launchManagementMenu(slot: Node, menu: String) {
  val screen = stringToManagementScreen(menu)
  if (screen != null) {
    val control = slot.getChildren().firstOrNull() as? Management
      ?: launchMenu(slot, "res://gui/menus/Management.tscn") as? Management

    control?.setActiveTab(screen)
  }
}

fun jobInterviewConversation() =
  ConversationDefinition(
    message = "Hey, friend!  You wanna make more money?",
    options = listOf(
      ConversationOption(
        title = "Hire",
        events = { _, player, other ->
          listOf(
            Event(hiredNpc, player, other),
            Event(joinedPlayer, other, player),
          )
        }
      ),
      ConversationOption(
        title = "Leave",
      ),
    ),
  )

fun manageMenu(slot: Node, player: Player?) {
  val interactingWith = player?.interactingWith
  val menu = player?.menu
  val slotHasMenu = slot.getChildCount() > 0
  if (interactingWith != null) {
    if (!slotHasMenu) {
      val screen = instantiateScene<Conversation>("res://gui/menus/Conversation.tscn")!!
      screen.definition = jobInterviewConversation()
      launchMenu(slot, screen)
    }
  } else if (menu != null) {
    if (!slotHasMenu || menu != lastMenu) {
      if (menu == gameOverScreen) {
        launchMenu(slot, showGameOverScreen())
      } else
        launchManagementMenu(slot, menu)
    }
  } else {
    if (slotHasMenu)
      slot.getChild(0)?.queueFree()
  }

  lastMenu = menu
}
