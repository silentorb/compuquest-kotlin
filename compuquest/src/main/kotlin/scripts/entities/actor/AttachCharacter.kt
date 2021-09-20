package scripts.entities.actor

import compuquest.simulation.general.componentGroup
import godot.Node
import godot.Resource
import godot.annotation.*

@Tool
@RegisterClass
class AttachCharacter : Node() {

  @Export
  @RegisterProperty
  var creature: Resource? = null

  @Export
  @RegisterProperty
  var faction: String = ""

  @Export
  @RegisterProperty
  var healthValue: Int = 1

  @RegisterFunction
  override fun _ready() {
	addToGroup(componentGroup)
  }

//  @RegisterFunction
//  override fun _process(delta: Double) {
//	if (depiction != "" && depiction != lastDepiction) {
//	  val sprite = getParent()?.findNode("sprite")
//	  sprite?.set("animation", depiction)
//	  lastDepiction = depiction
//	}
//  }
}
