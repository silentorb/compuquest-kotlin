package scripts.gui

import compuquest.simulation.input.Commands
import godot.Button
import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf
import scripts.Global
import silentorb.mythic.happening.Event

@RegisterClass
class Conversation : Node() {

  // This function needs to be snake case because Godot-JVM is converting everything to snake case under the hood
  @RegisterFunction
  fun on_pressed(index: Int) {
	val player = Global.getPlayer()!!
	val other = player.value.interactingWith!!
	if (index == 0) {
	  Global.addCommand(Event(Commands.hiredNpc, player.key, other))
	  Global.addCommand(Event(Commands.joinedPlayer, other, player.key))
	}
	Global.addPlayerCommand(Commands.finishInteraction)
  }

  @RegisterFunction
  override fun _ready() {
	val buttons = findNode("buttons")!!
	val message = findNode("message")!! as Label
	message.text = "Hey, friend!  You wanna make more money?"

	val options = listOf(
	  "Hire",
	  "Leave",
	)

	options.forEachIndexed { index, title ->
	  val button = Button()
	  button.text = title
	  button.connect("pressed", this, "on_pressed", variantArrayOf(index))
	  buttons.addChild(button)
	}
  }
}
