package scripts.world

import godot.KinematicBody
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.Tool
import silentorb.mythic.godoting.findChildren
import silentorb.mythic.godoting.tempCatch

@Tool
@RegisterClass
class RootNode : Node() {

  @RegisterFunction
  override fun _ready() {
    tempCatch {
      val playerNodes = findChildren(this) { it.name == "player" && it is KinematicBody }
      if (playerNodes.size > 1) {
        val (rootPlayers, testPlayers) = playerNodes.partition { it.getParent() == this }
        val removedPlayers = if (rootPlayers.none())
          testPlayers.drop(1)
        else
          rootPlayers.drop(1) + testPlayers

        for (node in removedPlayers) {
          tempCatch {
            node.queueFree()
          }
        }
      }
    }
  }
}
