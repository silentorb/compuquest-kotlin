package scripts

import compuquest.app.AppState
import compuquest.app.newAppState
import compuquest.definition.newDefinitions
import compuquest.simulation.general.Hand
import compuquest.simulation.general.newHandCommand
import compuquest.simulation.happening.Event
import compuquest.simulation.updating.updateWorld
import godot.Engine
import godot.Input
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class Global : Node() {
  var appState: AppState? = null
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
  }

  init {
    instance = this
  }

  fun restartGame() {
    val tree = getTree()
    val root = tree?.root
    if (root != null) {
      appState = newAppState(root, definitions)
      tree.reloadCurrentScene()
    }
  }

  @RegisterFunction
  override fun _process(delta: Double) {
    if (Input.isActionJustReleased("newGame")) {
      restartGame()
    }
  }

  @RegisterFunction
  override fun _physicsProcess(delta: Double) {
    if (!Engine.editorHint) {
      val state = appState
      if (state == null) {
        val root = getTree()?.root
        if (root != null) {
          appState = newAppState(root, definitions)
        }
      } else {
        val commands = eventQueue.toList()
        eventQueue.clear()
        appState = state.copy(
          world = updateWorld(commands, state.world)
        )
      }
    }
  }
}
