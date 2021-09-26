package scripts.gui

import godot.*
import silentorb.mythic.godoting.instantiateScene
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.godoting.tempCatch

@RegisterClass
class Hud : Node() {
  var slot: Node? = null
  var interact: Control? = null
  var debugText: Label? = null
	var lastMenu: String? = null

  @RegisterFunction
  override fun _ready() {
	slot = findNode("slot")
	interact = findNode("interact") as? Control
	debugText = findNode("debug") as? Label
  }

  fun launchMenu(menu: Node) {
	val localSlot = slot
	if (localSlot != null) {
	  clearChildren(localSlot)
	  localSlot.addChild(menu)
	}
  }

  fun launchMenu(scenePath: String): Node? {
	val menu = instantiateScene<Node>(scenePath)
	return if (menu != null) {
	  launchMenu(menu)
	  menu
	} else
	  null
  }

  fun launchManagementMenu(menu: String) {
	val screen = stringToManagementScreen(menu)
	if (screen != null) {
	  val control = slot?.getChildren()?.firstOrNull() as? Management
		?: launchMenu("res://gui/menus/Management.tscn") as? Management

	  control?.setActiveTab(screen)
	}
  }

  @RegisterFunction
  override fun _process(delta: Double) {
	tempCatch {
	  val world = Global.world
	  val deck = world?.deck
	  val player = deck?.players?.values?.firstOrNull()
	  val canInteractWith = player?.canInteractWith
	  val interactingWith = player?.interactingWith
	  val menu = player?.menu
	  interact!!.visible = canInteractWith != null
	  debugText?.text = Global.instance?.debugText ?: ""
	  val localSlot = slot
	  if (localSlot != null) {
		val slotHasMenu = localSlot.getChildCount() > 0
		if (interactingWith != null) {
		  if (!slotHasMenu)
			launchMenu("res://gui/menus/Conversation.tscn")
		} else if (menu != null) {
		  if (!slotHasMenu || menu != lastMenu) {
			if (menu == gameOverScreen) {
			  launchMenu(showGameOverScreen())
			} else
			  launchManagementMenu(menu)
		  }
		} else {
		  if (slotHasMenu)
			localSlot.getChild(0)?.queueFree()
		}
	  }

			lastMenu = menu
	}
  }
}
