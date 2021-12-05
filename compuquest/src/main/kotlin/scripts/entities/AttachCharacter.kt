package scripts.entities

import compuquest.population.componentGroup
import godot.Node
import godot.annotation.*

@Tool
@RegisterClass
class AttachCharacter : Node() {

  @Export
  @RegisterProperty
  var type: String = ""

  @RegisterFunction
  override fun _ready() {
	addToGroup(componentGroup)
  }
}
