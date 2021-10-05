package compuquest.clienting.gui

import compuquest.simulation.input.Commands
import scripts.Global

fun <Context>activateMenuItem(context: Context, item: MenuItem<Context>) {
  val eventSource = item.events
  when {
    eventSource != null -> {
      val events = eventSource(context)
      for (event in events) {
        Global.addEvent(event)
      }
      Global.addPlayerEvent(Commands.menuBack)
    }
    item.address != null -> {
      Global.addPlayerEvent(Commands.navigate, item.address)
    }
    else -> {
      Global.addPlayerEvent(Commands.menuBack)
    }
  }
}
