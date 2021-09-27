package scripts.gui

import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
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

  @RegisterFunction
  override fun _process(delta: Double) {
	tempCatch {
	  val player = Global.getPlayer()?.value
	  val canInteractWith = player?.canInteractWith
	  interact!!.visible = canInteractWith != null
	  debugText?.text = Global.instance?.debugText ?: ""
	  val localSlot = slot
	  if (localSlot != null) {
		manageMenu(localSlot, player)
	  }
	}
  }
}
