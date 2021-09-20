package scripts.entities.actor

import compuquest.serving.componentGroup
import godot.Node
import godot.annotation.*

@RegisterClass
class AttachAccessory : Node() {

  @Export
  @RegisterProperty
  var definition: String = ""

  @RegisterFunction
  override fun _ready() {
    addToGroup(componentGroup)
  }

}
