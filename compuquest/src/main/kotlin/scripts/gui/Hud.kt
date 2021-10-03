package scripts.gui

import compuquest.clienting.getPlayerMenuStack
import compuquest.clienting.gui.manageMenu
import compuquest.clienting.setPlayerMenuStack
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.ent.Key
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
	  val player = Global.getPlayer()
	  val canInteractWith = player?.value?.canInteractWith
	  interact!!.visible = canInteractWith != null
	  debugText?.text = Global.instance?.debugText ?: ""
	  val localSlot = slot
	  if (localSlot != null && player != null) {
	  	val client = Global.instance!!.client
	  	val menuStack = getPlayerMenuStack(client, player.key)
			val nextMenuStack = manageMenu(localSlot, player.key, player.value, menuStack)
	  	setPlayerMenuStack(client, player.key, nextMenuStack)
	  }
	}
  }
}
