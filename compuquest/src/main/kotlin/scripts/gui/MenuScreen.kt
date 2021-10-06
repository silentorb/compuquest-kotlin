package scripts.gui

import compuquest.clienting.gui.GameContext
import compuquest.clienting.gui.MenuContent
import compuquest.clienting.gui.activateMenuItem
import godot.Button
import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global

@RegisterClass
class MenuScreen : Node() {

  var content: MenuContent<GameContext>? = null

  // This function needs to be snake case because Godot-JVM is converting everything to snake case under the hood
  @RegisterFunction
  fun on_pressed(index: Int) {
	val world = Global.world
	val localContent = content
	if (world != null && localContent != null) {
	  val item = localContent.items[index]
	  val context = GameContext(
		world = world,
		actor = Global.getPlayer()!!.key
	  )
	  activateMenuItem(context, item)
	}
  }

  @RegisterFunction
  override fun _ready() {
	val buttons = findNode("buttons")!!
	val message = findNode("message")!! as Label
	val localContent = content
	if (localContent != null) {
	  message.text = localContent.message.joinToString("\n\n")

	  val context = GameContext(
		world = Global.world!!,
		actor = Global.getPlayer()!!.key,
	  )
	  localContent.items.forEachIndexed { index, option ->
		val button = Button()
		button.text = option.title
		val enabled = option.enabled
		button.disabled = !(enabled == null || enabled(context, null))
		button.connect("pressed", this, "on_pressed", variantArrayOf(index))
		buttons.addChild(button)
	  }
	}
  }
}
