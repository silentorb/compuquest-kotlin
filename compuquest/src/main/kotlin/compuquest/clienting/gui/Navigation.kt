package compuquest.clienting.gui

import compuquest.simulation.input.Commands
import scripts.Global
import silentorb.mythic.ent.Id

fun navigateBack(player: Id) {
	Global.addPlayerEvent(Commands.menuBack, player)
}

fun <Context> activateMenuItem(context: Context, player: Id, item: MenuItem<Context>) {
	val eventSource = item.events
	when {
		eventSource != null -> {
			val events = eventSource(context)
			for (event in events) {
				Global.addEvent(event)
			}
			if (item.address != null)
				Global.addPlayerEvent(Commands.navigate, player, item.address)
			else
				navigateBack(player)
		}
		item.address != null -> {
			Global.addPlayerEvent(Commands.drillDown, player, item.address)
		}
		else -> {
			navigateBack(player)
		}
	}
}
