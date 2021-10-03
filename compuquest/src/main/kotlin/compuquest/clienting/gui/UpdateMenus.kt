package compuquest.clienting.gui

import compuquest.simulation.general.Player
import compuquest.simulation.input.Commands
import godot.Node
import scripts.gui.Management
import scripts.gui.MenuScreen
import scripts.gui.gameOverScreen
import scripts.gui.showGameOverScreen
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.godoting.tempCatchStatement
import silentorb.mythic.happening.handleEvents

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

fun newManagementMenu(slot: Node, screen: ManagementScreens): Management? {
  val existing = slot.getChildren().firstOrNull() as? Management
  val control = existing
    ?: launchMenu(slot, "res://gui/menus/Management.tscn") as? Management

  control?.setActiveTab(screen)
  return if (existing == null)
    control
  else
    null
}

fun newMenuScreen(content: MenuContent): MenuScreen {
  val screen = instantiateScene<MenuScreen>("res://gui/menus/MenuScreen.tscn")!!
  screen.content = content
  return screen
}


fun interactionAddress(player: Player): MenuAddress? {
  val target = player.canInteractWith?.target
  return if (target != null)
    MenuAddress(Menus.conversation, target)
  else
    null
}

fun updateMenuStack(player: Player) = handleEvents<MenuStack> { event, menuStack ->
  when (event.type) {
    Commands.interact -> listOfNotNull(interactionAddress(player))
    Commands.finishInteraction -> listOf()
    Commands.manageMembers -> listOf(ManagementScreens.members.name)
    Commands.manageQuests -> listOf(ManagementScreens.quests.name)
    Commands.menuBack -> menuStack.dropLast(1)
    else -> menuStack
  }
}

fun syncGuiToState(slot: Node, actor: Id, player: Player?, lastMenu: Any?, menuStack: MenuStack) {
  tempCatchStatement {
    val slotHasMenu = slot.getChildCount() > 0
    val menu = menuStack.lastOrNull()
    if (menu != null) {
      if (!slotHasMenu || lastMenu != menu) {
        val screen = when (menu) {
          gameOverScreen -> showGameOverScreen()
          ManagementScreens.members.name -> newManagementMenu(slot, ManagementScreens.members)
          ManagementScreens.quests.name -> newManagementMenu(slot, ManagementScreens.quests)
          is MenuAddress -> when (menu.key) {
            Menus.conversation -> conversationMenu(actor, menu.context as? Id)
            else -> null
          }
          else -> null
        }
        if (screen != null) {
          launchMenu(slot, screen)
        }
      }
      listOf(menu)
    } else if (slotHasMenu)
      slot.getChild(0)?.queueFree()
  }
}
