package scripts.entities.actor

import compuquest.simulation.general.isFriendly
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import scripts.Global

@RegisterClass
class PlayerVisible : Spatial() {

  var isInitialized: Boolean = false
  var _isVisible: Boolean = true

  @RegisterFunction
  override fun _physicsProcess(delta: Double) {
    if (isInitialized && _isVisible)
      return

    val player = Global.getPlayer()
    val world = Global.world
    if (player != null && world != null) {
      val parent = getParent() as Spatial
      val actor = world.bodies.entries.firstOrNull { it.value == parent }?.key
      val character = world.deck.characters[actor]
      if (character != null) {
        val isVisible = if (character.enemyVisibilityRange == 0f ||
          isFriendly(world.factionRelationships, character.faction, player.value.faction)
        )
          true
        else {
          val playerBody = world.deck.bodies[player.key]!!
          val distance = playerBody.translation.distanceTo(parent.translation)
          distance <= character.enemyVisibilityRange
        }
        visible = isVisible
        _isVisible = isVisible
        isInitialized = true
      }
    }
  }
}
