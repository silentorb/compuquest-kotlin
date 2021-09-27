package scripts.gui

import compuquest.simulation.input.Commands
import godot.Button
import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global

@RegisterClass
class Conversation : Node() {

  var definition: ConversationDefinition? = null

  // This function needs to be snake case because Godot-JVM is converting everything to snake case under the hood
  @RegisterFunction
  fun on_pressed(index: Int) {
	val world = Global.world
	if (world != null) {
	  val player = Global.getPlayer()!!
	  val other = player.value.interactingWith!!
	  val eventSource = definition!!.options[index].events
	  if (eventSource != null) {
		val events = eventSource(world, player.key, other)
		for (event in events) {
		  Global.addEvent(event)
		}
	  }
	}
	Global.addPlayerEvent(Commands.menuBack)
  }

  @RegisterFunction
  override fun _ready() {
	val buttons = findNode("buttons")!!
	val message = findNode("message")!! as Label
	message.text = definition!!.message

	definition!!.options.forEachIndexed { index, option ->
	  val button = Button()
	  button.text = option.title
	  button.connect("pressed", this, "on_pressed", variantArrayOf(index))
	  buttons.addChild(button)
	}
  }
}
