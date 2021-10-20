package scripts

import compuquest.app.newGame
import compuquest.clienting.*
import compuquest.definition.newDefinitions
import silentorb.mythic.godoting.tempCatch
import compuquest.simulation.general.Player
import compuquest.simulation.general.World
import compuquest.simulation.general.getPlayer
import compuquest.simulation.input.Commands
import compuquest.clienting.input.gatherDefaultPlayerInput
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
import silentorb.mythic.happening.Events

enum class InitMode {
  delayInit,
  readyInitOrInitialized,
  delayRestart,
  readyRestart,
}

@RegisterClass
class Global : Node() {
  var worlds: List<World> = listOf()
  val definitions = newDefinitions()
  var client: Client = newClient()

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

    fun getMenuStack() =
      instance!!.client.menuStack
  }

  init {
    instance = this
  }

  var initMode: InitMode = InitMode.delayInit

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
      initMode = InitMode.delayRestart
    }
  }

  @RegisterFunction
  override fun _ready() {
    val hud = instantiateScene<Node>("res://gui/hud/Hud.tscn")!!
    addChild(hud)
  }

  fun updateEvents(): Events {
    val events = eventQueue.toList() + gatherDefaultPlayerInput(worlds.last())
    eventQueue.clear()
    return events
  }

  fun updateWorlds(events: Events, worlds: List<World>, delta: Float) {
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
        if (Input.isActionJustReleased("quit"))
          getTree()!!.quit()

        val localWorlds = worlds
        when (initMode) {

          InitMode.delayRestart -> {
            val tree = getTree()
            tree!!.reloadCurrentScene()
            initMode = InitMode.readyRestart
          }

          InitMode.readyRestart -> {
            val tree = getTree()
            val root = tree?.root
            // This needs to happen *after* the scene is reloaded
            worlds = listOf(newGame(root!!, definitions))
            client = restartClient(client)
            initMode = InitMode.readyInitOrInitialized
          }

          InitMode.readyInitOrInitialized -> {
            if (localWorlds.none()) {
              val root = getTree()?.root
              if (root != null) {
                worlds = listOf(newGame(root, definitions))
              }
            } else {
              val events = updateEvents()
              val previousClient = client
              val nextClient = updateClient(localWorlds.lastOrNull(), events, previousClient)
              val clientEvents = eventsFromClient(getPlayer()!!.key, nextClient, previousClient)
              client = nextClient
              updateWorlds(events + clientEvents, localWorlds, delta.toFloat())
            }
          }
          InitMode.delayInit -> {
            // Delay initialization one frame to ensure nodes deleted during _ready are completely removed.
            // This should not be needed and is either a Godot design flaw or bug
            initMode = InitMode.readyInitOrInitialized
          }
        }
      }
    }
  }
}

fun debugLog(message: String) {
  val previous = Global.instance?.debugText ?: ""
  Global.instance?.debugText = listOf(previous, message).joinToString("\n")
}
