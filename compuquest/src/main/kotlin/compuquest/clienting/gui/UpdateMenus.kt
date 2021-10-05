package compuquest.clienting.gui

import compuquest.simulation.general.Player
import compuquest.simulation.general.World
import compuquest.simulation.input.Commands
import godot.Node
import scripts.gui.Management
import scripts.gui.MenuScreen
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.Key
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.godoting.tempCatch
import silentorb.mythic.godoting.tempCatchStatement
import silentorb.mythic.happening.handleEvents

fun mountScreen(slot: Node, menu: Node) {
  clearChildren(slot)
  slot.addChild(menu)
}

fun mountScreen(slot: Node, scenePath: String): Node? {
  val menu = instantiateScene<Node>(scenePath)
  return if (menu != null) {
    mountScreen(slot, menu)
    menu
  } else
    null
}

fun newMenuScreen(content: MenuContent): MenuScreen {
  return tempCatch {
    val screen = instantiateScene<MenuScreen>("res://gui/menus/MenuScreen.tscn")!!
    screen.content = content
    screen
  }
}

fun newConversationMenu(content: MenuContent): MenuScreen =
  newMenuScreen(
    content.copy(items = content.items + leaveMenuItem)
  )

fun interactionAddress(player: Player): MenuAddress? {
  val target = player.canInteractWith?.target
  return if (target != null)
    MenuAddress(Screens.conversation, target)
  else
    null
}

fun updateMenuStack(player: Player) = handleEvents<MenuStack> { event, menuStack ->
  when (event.type) {
    Commands.interact -> listOfNotNull(interactionAddress(player))
    Commands.finishInteraction -> listOf()
    Commands.manageMembers -> listOf(MenuAddress(Screens.manageMembers))
    Commands.manageQuests -> listOf(MenuAddress(Screens.manageQuests))
    Commands.menuBack -> menuStack.dropLast(1)
    Commands.navigate -> menuStack.plus(event.value as MenuAddress)
    else -> menuStack
  }
}

fun syncGuiToState(slot: Node, actor: Id, world: World, lastMenu: Any?, menuStack: MenuStack) {
  tempCatchStatement {
    val slotHasMenu = slot.getChildCount() > 0
    val address = menuStack.lastOrNull()
    if (address != null) {
      if (!slotHasMenu || lastMenu != address) {
        val screen = gameScreens[address.key]
//        val screen = when (menu) {
//          gameOverScreen -> showGameOverScreen()
//          ManagementScreens.members.name -> newManagementMenu(slot, ManagementScreens.members)
//          ManagementScreens.quests.name -> newManagementMenu(slot, ManagementScreens.quests)
//          is MenuAddress -> when (menu.key) {
//            Screens.conversation -> conversationMenu(actor, menu.context as? Id)
//            else -> null
//          }
//          else -> null
//        }

        if (screen != null) {
          val context = GameContext(
            world = world,
            actor = actor,
          )
          val content = screen.content(context, address.argument)
          mountScreen(slot, content)
        }
      }
      listOf(address)
    } else if (slotHasMenu)
      slot.getChild(0)?.queueFree()
  }
}
