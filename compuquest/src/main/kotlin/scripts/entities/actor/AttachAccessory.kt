package scripts.entities.actor

import godot.Node
import godot.annotation.*

@RegisterClass
class AttachAccessory : Node() {

  @Export
  @RegisterProperty
  var definition: String = ""
}
