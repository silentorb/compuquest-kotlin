package compuquest.app

import compuquest.serving.newGameState
import compuquest.simulation.definition.Definitions

fun newAppState(definitions: Definitions): AppState {
  val game = newGameState(definitions)
  return AppState(
    game = game,
  )
}
