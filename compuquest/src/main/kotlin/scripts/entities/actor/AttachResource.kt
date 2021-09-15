package scripts.entities.actor

import compuquest.simulation.general.componentGroup
import godot.Node
import godot.annotation.*

@RegisterClass
class AttachResource : Node() {

  @Export
  @RegisterProperty
  var resource: String = ""

  @Export
  @RegisterProperty
  var amount: Int = 0

  @RegisterFunction
  override fun _ready() {
    addToGroup(componentGroup)
  }

}
