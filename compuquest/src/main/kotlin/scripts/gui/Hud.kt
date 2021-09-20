package scripts.gui

import silentorb.mythic.godoting.instantiateScene
import godot.Control
import godot.Node
import godot.PackedScene
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.godoting.clearChildren
import silentorb.mythic.godoting.tempCatch

@RegisterClass
class Hud : Node() {
  var slot: Node? = null
  var interact: Control? = null

  @RegisterFunction
  override fun _ready() {
	slot = findNode("slot")
	interact = findNode("interact") as? Control
  }

  fun launchMenu() {
	val menu = instantiateScene<Node>("res://gui/menus/Conversation.tscn")
	val localSlot = slot
	if (menu != null && localSlot != null) {
	  clearChildren(localSlot)
	  localSlot.addChild(menu)
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
	  interact!!.visible = canInteractWith != null
	  val localSlot = slot
	  if (localSlot != null) {
		val hasMenu = localSlot.getChildCount() > 0
		if (interactingWith != null) {
		  if (!hasMenu)
			launchMenu()
		} else {
		  if (hasMenu)
			localSlot.getChild(0)?.queueFree()
		}
	  }
	}
  }
}
