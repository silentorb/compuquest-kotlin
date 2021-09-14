package scripts.entities.actor

import compuquest.simulation.general.*
import godot.Engine
import godot.Node
import godot.Sprite
import godot.annotation.*

@RegisterClass
@Tool
class AddCharacter : Node() {

  @Export
  @RegisterProperty
  var characterName: String = ""

  @Export
  @RegisterProperty
  var depiction: String = ""

  @Export
  @RegisterProperty
  var faction: String = ""

  @Export
  @RegisterProperty
  var healthValue: Int = 0

  @Export
  @RegisterProperty
  var healthMax: Int = 0

  @RegisterFunction
  override fun _ready() {
	addToGroup(componentGroup)
  }

  @RegisterFunction
  override fun _process(delta: Double) {
	if (Engine.editorHint) {
	  val sprite = getParent()?.findNode("sprite") as? Sprite
	  sprite?.set("animation", depiction)
	}
  }
}
