package scripts.gui

import compuquest.simulation.input.Commands
import scripts.Global
import silentorb.mythic.happening.Event

val gameOverScreen = "gameOverScreen"

fun showGameOverScreen(): Prompt =
  newPrompt("Game Over", "You have died!") {
    Global.addEvent(Event(Commands.newGame))
  }
