package scripts.entities.actor

import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class PlayerBody : Spatial() {

  @RegisterFunction
  override fun _ready() {

  }

  @RegisterFunction
  override fun _process(delta: Double) {
  }
}
