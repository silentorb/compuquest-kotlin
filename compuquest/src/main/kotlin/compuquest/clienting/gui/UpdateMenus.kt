package compuquest.clienting.gui

import compuquest.simulation.general.Player
import compuquest.simulation.general.World
import compuquest.simulation.input.Commands
import godot.Node
import scripts.Global
import scripts.gui.MenuScreen
import silentorb.mythic.ent.Id
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

@Deprecated("Use newPopupMenu instead")
fun newMenuScreen(content: GameMenuContent): MenuScreen {
	return tempCatch {
		val screen = instantiateScene<MenuScreen>("res://gui/menus/MenuScreen.tscn")!!
		screen.message = content.message
		screen.items = content.items
		screen
	}
}

fun newPopupMenu(title: String, actor: Id, items: List<MenuItem<GameContext>>): MenuScreen {
	val screen = instantiateScene<MenuScreen>("res://gui/menus/MenuPopup.tscn")!!
	screen.title = title
	screen.items = items
	screen.actor = actor
	return screen
}

fun newConversationMenu(content: GameMenuContent): MenuScreen =
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

fun toMenuAddress(value: Any?): MenuAddress =
	when (value) {
		is MenuAddress -> value
		is String -> MenuAddress(value)
		null -> throw Error("Null MenuAddress")
		else -> throw Error("$value cannot be converted to a MenuAddress")
	}

fun tryNavigateBack(menuStack: MenuStack) =
	if (!uncancellableScreens.contains(menuStack.lastOrNull()?.key)) menuStack.dropLast(1) else menuStack

fun updateMenuStack(player: Player) = handleEvents<MenuStack> { event, menuStack ->
	when (event.type) {
		Commands.interact -> listOfNotNull(interactionAddress(player))
		Commands.finishInteraction -> listOf()
		Commands.menuBack -> tryNavigateBack(menuStack)
		Commands.drillDown -> menuStack.plus(toMenuAddress(event.value))
		Commands.navigate -> listOf(toMenuAddress(event.value))
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

				if (screen != null) {
					val context = GameContext(
						world = world,
						actor = actor,
					)
					val content = screen.content(context, address.argument)
					mountScreen(slot, content)
					val playerMenus = Global.instance!!.playerMenus
					if (content is MenuScreen) {
						playerMenus[actor] = content
					} else {
						playerMenus.remove(actor)
					}
				}
			}
			listOf(address)
		} else if (slotHasMenu) {
			val screen = slot.getChild(0)
			if (screen is HasOnClose) {
				screen.onClose()
			}
			slot.getChild(0)?.queueFree()
			val playerMenus = Global.instance!!.playerMenus
			playerMenus.remove(actor)
		}
	}
}
