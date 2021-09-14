package scripts.entities.actor

import compuquest.simulation.general.componentGroup
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.Tool

@RegisterClass
class AttachPlayer : Node() {

  @RegisterFunction
  override fun _ready() {
    addToGroup(componentGroup)
  }
}
