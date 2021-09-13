package scripts

import compuquest.app.AppState
import compuquest.app.newAppState
import compuquest.definition.newDefinitions
import compuquest.serving.newGameState
import compuquest.simulation.general.Command
import godot.Engine
import godot.Input
import godot.InputEvent
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class Global : Node() {
  var appState: AppState? = null
  val definitions = newDefinitions()

  companion object {
    var instance: Global? = null
    private var commandQueue: MutableList<Command> = mutableListOf()

    fun addCommand(command: Command) {
      commandQueue.add(command)
    }
  }

  init {
    instance = this
    if (!Engine.editorHint) {
      appState = newAppState(definitions)
    }
  }

  fun restartGame() {
    appState = newAppState(definitions)
    getTree()?.reloadCurrentScene()
  }

  @RegisterFunction
  override fun _ready() {

  }

  @RegisterFunction
  override fun _process(delta: Double) {
    if (Input.isActionJustReleased("newGame")) {
      restartGame()
    }
  }

}
