package scripts.gui

import compuquest.clienting.getPlayerMenuStack
import compuquest.clienting.gui.syncGuiToState
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global
import silentorb.mythic.godoting.tempCatch

@RegisterClass
class Hud : Control() {
  var slot: Node? = null
  var interact: Control? = null
  var debugText: Label? = null
  var lastMenu: Any? = null

  @RegisterFunction
  override fun _ready() {
	slot = findNode("slot")
	interact = findNode("interact") as? Control
	debugText = findNode("debug") as? Label
  }

  @RegisterFunction
  override fun _process(delta: Double) {
	tempCatch {
		val client = Global.instance?.client
		if (client != null) {
			visible = client.options.ui.showHud
		}
	  val player = Global.getPlayer()
	  val canInteractWith = player?.value?.canInteractWith
	  interact!!.visible = canInteractWith != null
	  debugText?.text = Global.instance?.debugText ?: ""
	  val localSlot = slot
	  if (client != null && player != null) {
		val menuStack = getPlayerMenuStack(client, player.key)

		if (localSlot != null) {
		  syncGuiToState(localSlot, player.key, Global.world!!, lastMenu, menuStack)
			lastMenu = menuStack.lastOrNull()
		}
	  }
	}
  }
}
