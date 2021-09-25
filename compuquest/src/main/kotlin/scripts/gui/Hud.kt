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

  fun launchMenu(scenePath: String) {
	val menu = instantiateScene<Node>(scenePath)
	if (menu != null) {
		launchMenu(menu)
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
	  val managementMenu = player?.menu
	  interact!!.visible = canInteractWith != null
	  debugText?.text = Global.instance?.debugText ?: ""
	  val localSlot = slot
	  if (localSlot != null) {
		val slotHasMenu = localSlot.getChildCount() > 0
		if (interactingWith != null) {
		  if (!slotHasMenu)
			launchMenu("res://gui/menus/Conversation.tscn")
		} else if (managementMenu != null) {
		  if (!slotHasMenu) {
			if (managementMenu == gameOverScreen) {
				launchMenu(showGameOverScreen())
			} else
			  launchMenu("res://gui/menus/$managementMenu.tscn")
		  }
		} else {
		  if (slotHasMenu)
			localSlot.getChild(0)?.queueFree()
		}
	  }
	}
  }
}
