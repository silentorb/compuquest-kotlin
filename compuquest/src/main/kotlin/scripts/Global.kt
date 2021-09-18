package scripts

import compuquest.app.newGame
import compuquest.definition.newDefinitions
import compuquest.simulation.general.Hand
import compuquest.simulation.general.World
import compuquest.simulation.general.newHandCommand
import silentorb.mythic.happening.Event
import compuquest.simulation.updating.updateWorld
import godot.Engine
import godot.Input
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class Global : Node() {
  var worlds: List<World> = listOf()
  val definitions = newDefinitions()

  companion object {
    var instance: Global? = null
    private var eventQueue: MutableList<Event> = mutableListOf()

    fun addCommand(event: Event) {
      eventQueue.add(event)
    }

    fun newHand(hand: Hand) {
      addCommand(newHandCommand(hand))
    }

    val world: World?
      get() = instance?.worlds?.lastOrNull()
  }

  init {
    instance = this
  }

  var restarting = false

  fun restartGame() {
    val tree = getTree()
    val root = tree?.root
    if (root != null) {
      val world = worlds.lastOrNull()
      if (world != null) {
        for (body in world.bodies.values) {
          body.queueFree()
        }
        for (sprites in world.sprites.values) {
          sprites.queueFree()
        }
      }
      // Wait until the frame has finished processing and the queued nodes are freed before
      // continuing with the restarting process
      restarting = true
    }
  }

  @RegisterFunction
  override fun _process(delta: Double) {
    if (!Engine.editorHint) {
      if (Input.isActionJustReleased("newGame")) {
        restartGame()
      }
    }
  }

  @RegisterFunction
  override fun _physicsProcess(delta: Double) {
    if (!Engine.editorHint) {
      val localWorlds = worlds
      if (restarting) {
        val tree = getTree()
        val root = tree?.root
        tree!!.reloadCurrentScene()
        // This needs to happen after the scene is reloaded
        worlds = listOf(newGame(root!!, definitions))
        restarting = false
      } else if (localWorlds.none()) {
        val root = getTree()?.root
        if (root != null) {
          worlds = listOf(newGame(root, definitions))
        }
      } else {
        val commands = eventQueue.toList()
        eventQueue.clear()
        worlds = worlds.plus(updateWorld(commands, delta.toFloat(), localWorlds)).takeLast(2)
      }
    }
  }
}
