package scripts.gui

import compuquest.simulation.input.Commands
import scripts.Global
import silentorb.mythic.happening.Event
import silentorb.mythic.happening.newEvent

val gameOverScreen = "gameOverScreen"

fun showGameOverScreen(): Prompt =
  newPrompt("Game Over", "You have died!") {
    Global.addEvent(newEvent(Commands.newGame))
  }
