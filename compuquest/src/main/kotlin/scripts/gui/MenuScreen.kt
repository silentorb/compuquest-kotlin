package scripts.gui

import compuquest.clienting.gui.MenuContent
import compuquest.simulation.input.Commands
import godot.Button
import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global

@RegisterClass
class MenuScreen : Node() {

  var content: MenuContent? = null

  // This function needs to be snake case because Godot-JVM is converting everything to snake case under the hood
  @RegisterFunction
  fun on_pressed(index: Int) {
	val world = Global.world
	val localContent = content
	if (world != null && localContent != null) {
	  val player = Global.getPlayer()!!
    val option = localContent.items[index]
	  val eventSource = option.events
	  if (eventSource != null) {
		val events = eventSource(world, player.key)
		for (event in events) {
		  Global.addEvent(event)
		}
	  }
    else if (option.content != null) {

    }
	}
	Global.addPlayerEvent(Commands.menuBack)
  }

  @RegisterFunction
  override fun _ready() {
		val buttons = findNode("buttons")!!
		val message = findNode("message")!! as Label
		val localContent = content
		if (localContent != null) {
			message.text = localContent.message

			localContent.items.forEachIndexed { index, option ->
				val button = Button()
				button.text = option.title
				button.connect("pressed", this, "on_pressed", variantArrayOf(index))
				buttons.addChild(button)
			}
		}
	}
}
