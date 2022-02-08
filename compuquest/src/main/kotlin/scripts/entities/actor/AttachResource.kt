package scripts.entities.actor

import godot.Node
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty

@RegisterClass
class AttachResource : Node() {

  @Export
  @RegisterProperty
  var resource: String = ""

  @Export
  @RegisterProperty
  var amount: Int = 0
}
