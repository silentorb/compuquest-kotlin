package scripts.gui

import godot.Control
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global

@RegisterClass
class Hud : Node() {
//  var screen: Node? = null
  var interact: Control? = null

  @RegisterFunction
  override fun _ready() {
//	screen = findNode("screen")
	interact = findNode("interact") as? Control
  }

  @RegisterFunction
  override fun _process(delta: Double) {
	val world = Global.world
	val deck = world?.deck
	val player = deck?.players?.values?.firstOrNull()
	val canInteractWith = player?.canInteractWith
	interact!!.visible = canInteractWith != null
  }
}
