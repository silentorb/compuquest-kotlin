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

  @Export
  @RegisterProperty
  var includeFees: Boolean = true

  @RegisterFunction
  override fun _ready() {
	addToGroup(componentGroup)
  }
}
