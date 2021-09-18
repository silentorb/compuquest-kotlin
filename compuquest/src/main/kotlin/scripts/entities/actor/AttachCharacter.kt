package scripts.entities.actor

import compuquest.simulation.general.componentGroup
import godot.Node
import godot.annotation.*

@Tool
@RegisterClass
class AttachCharacter : Node() {

  @Export
  @RegisterProperty
  var depiction: String = ""
  var lastDepiction: String = ""

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
	if (depiction != "" && depiction != lastDepiction) {
	  val sprite = getParent()?.findNode("sprite")
	  sprite?.set("animation", depiction)
	  lastDepiction = depiction
	}
  }
}
