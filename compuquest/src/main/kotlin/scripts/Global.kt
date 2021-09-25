package scripts

import compuquest.app.newGame
import compuquest.definition.newDefinitions
import silentorb.mythic.godoting.tempCatch
import compuquest.simulation.general.Player
import compuquest.simulation.general.World
import compuquest.simulation.general.getPlayer
import compuquest.simulation.input.Commands
import compuquest.simulation.input.gatherDefaultPlayerInput
import silentorb.mythic.happening.Event
import compuquest.simulation.updating.updateWorld
import godot.Engine
import godot.Input
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import silentorb.mythic.debugging.checkDotEnvChanged
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene

@RegisterClass
class Global : Node() {
  var worlds: List<World> = listOf()
  val definitions = newDefinitions()

  @RegisterProperty
  var debugText: String = ""

  companion object {
    var instance: Global? = null
    private var eventQueue: MutableList<Event> = mutableListOf()

    fun addEvent(event: Event) {
      eventQueue.add(event)
    }

    fun addPlayerEvent(type: String, value: Any? = null) {
      val player = world?.deck?.players?.keys?.firstOrNull()
      addEvent((Event(type, player, value)))
    }

    val world: World?
      get() = instance?.worlds?.lastOrNull()

    fun getPlayer(): Map.Entry<Id, Player>? =
      getPlayer(world)
  }

  init {
    instance = this
  }

  var restarting = 0

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
      restarting = 1
    }
  }

  @RegisterFunction
  override fun _ready() {
    val hud = instantiateScene<Node>("res://gui/hud/Hud.tscn")!!
    addChild(hud)
  }

  fun updateWorlds(worlds: List<World>, delta: Float) {
    val events = eventQueue.toList() + gatherDefaultPlayerInput(worlds.last())
    eventQueue.clear()
    if (events.any { it.type == Commands.newGame }) {
      restartGame()
    } else {
      val nextWorld = updateWorld(events, delta, worlds)
      this.worlds = worlds.plus(nextWorld).takeLast(2)
    }
  }

  @RegisterFunction
  override fun _physicsProcess(delta: Double) {
    if (!Engine.editorHint) {
      if (getDebugBoolean("WATCH_DOT_ENV"))
        checkDotEnvChanged()

      debugText = ""
      tempCatch {
        val localWorlds = worlds
        if (restarting == 1) {
          val tree = getTree()
          tree!!.reloadCurrentScene()
          restarting = 2
        } else if (restarting == 2) {
          val tree = getTree()
          val root = tree?.root
          // This needs to happen *after* the scene is reloaded
          worlds = listOf(newGame(root!!, definitions))
          restarting = 0
        } else if (localWorlds.none()) {
          val root = getTree()?.root
          if (root != null) {
            worlds = listOf(newGame(root, definitions))
          }
        } else {
          updateWorlds(localWorlds, delta.toFloat())
        }
      }
    }
  }
}

fun debugLog(message: String) {
  val previous = Global.instance?.debugText ?: ""
  Global.instance?.debugText = listOf(previous, message).joinToString("\n")
}
