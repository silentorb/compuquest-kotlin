package compuquest.clienting.gui

import compuquest.simulation.general.World
import compuquest.simulation.input.Commands
import scripts.Global

fun activateMenuItem(world: World, item: MenuItem) {
  val player = Global.getPlayer()!!
  val eventSource = item.events
  if (eventSource != null) {
    val events = eventSource(world, player.key)
    for (event in events) {
      Global.addEvent(event)
    }
    Global.addPlayerEvent(Commands.menuBack)
  } else if (item.address != null) {
    Global.addPlayerEvent(Commands.navigate, item.address)
  } else {
    Global.addPlayerEvent(Commands.menuBack)
  }
}
