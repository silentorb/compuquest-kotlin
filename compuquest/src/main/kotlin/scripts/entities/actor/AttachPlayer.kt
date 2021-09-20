package scripts.entities.actor

import compuquest.serving.componentGroup
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class AttachPlayer : Node() {

  @RegisterFunction
  override fun _ready() {
    addToGroup(componentGroup)
  }
}
