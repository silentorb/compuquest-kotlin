package scripts

import compuquest.app.newGame
import compuquest.definition.newDefinitions
import silentorb.mythic.godoting.tempCatch
import compuquest.simulation.general.Hand
import compuquest.simulation.general.Player
import compuquest.simulation.general.World
import compuquest.simulation.general.newHandCommand
import silentorb.mythic.happening.Event
import compuquest.simulation.updating.updateWorld
import godot.Engine
import godot.Input
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.instantiateScene

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

    fun addPlayerCommand(type: String, value: Any? = null) {
      val player = world?.deck?.players?.keys?.firstOrNull()
      addCommand((Event(type, player, value)))
    }

    fun newHand(hand: Hand) {
      addCommand(newHandCommand(hand))
    }

    val world: World?
      get() = instance?.worlds?.lastOrNull()

    fun getPlayer(): Map.Entry<Id, Player>? =
      world?.deck?.players?.entries?.firstOrNull()
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
      tempCatch {
        val localWorlds = worlds
        if (restarting == 1) {
          val tree = getTree()
          tree!!.reloadCurrentScene()
          restarting = 2
        } else if (restarting == 2) {
          val tree = getTree()
          val root = tree?.root
          // This needs to happen after the scene is reloaded
          worlds = listOf(newGame(root!!, definitions))
          restarting = 0
        } else if (localWorlds.none()) {
          val root = getTree()?.root
          if (root != null) {
            worlds = listOf(newGame(root, definitions))
          }
        } else {
          val commands = eventQueue.toList()
          eventQueue.clear()
          worlds = localWorlds.plus(updateWorld(commands, delta.toFloat(), localWorlds)).takeLast(2)
          val nextWorld = worlds.lastOrNull()
          val player = nextWorld?.deck?.players?.entries?.firstOrNull()
          if (player != null) {
            val playerRigIsActive = player.value.interactingWith == null
            val body = nextWorld.bodies[player.key]
            body?.set("isActive", playerRigIsActive)
          }
        }
      }
    }
  }
}
