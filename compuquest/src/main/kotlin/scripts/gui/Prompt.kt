package scripts.gui

import godot.Label
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.godoting.tempCatch

@RegisterClass
class Prompt : Node() {

  var title: String = ""
  var body: String = ""
  var onOkay: () -> Unit = {}

  @RegisterFunction
  fun on_okay_pressed() {
	onOkay()
	queueFree()
  }

  @RegisterFunction
  override fun _ready() {
	(findNode("title") as? Label)?.text = title
	(findNode("body") as? Label)?.text = body
	findNode("okay")?.connect("pressed", this, "on_okay_pressed")
  }

  @RegisterFunction
  override fun _process(delta: Double) {
	tempCatch {
	}
  }
}

fun newPrompt(title: String, body: String, onOkay: () -> Unit): Prompt {
  return tempCatch {
	val prompt = instantiateScene<Prompt>("res://gui/menus/Prompt.tscn")!!
	prompt.title = title
	prompt.body = body
	prompt.onOkay = onOkay
	prompt
  }!!
}
