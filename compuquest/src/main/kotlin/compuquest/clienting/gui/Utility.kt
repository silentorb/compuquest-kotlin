package compuquest.clienting.gui

import compuquest.clienting.Client
import compuquest.clienting.isPrimaryPlayer
import scripts.Global
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.ent.Id
import silentorb.mythic.haft.PlayerMap

fun getMenuItemTitle(context: GameContext, item: GameMenuItem): String {
	val address = item.address
	return item.title ?: if (address != null) {
		val titleSource = gameScreens[address.key]?.title
		if (titleSource != null)
			titleSource(context, item)
		else
			throw Error("MenuItem has no title no screen could be found for its address")
	} else
		throw Error("Could not determine MenuItem title")
}

fun getGameContext(actor: Id): GameContext? {
	val world = Global.world
	return if (world != null) {
		GameContext(
			world = world,
			actor = actor
		)
	} else
		null
}

fun getFocusMode(playerCount: Int): FocusMode =
	if (getDebugBoolean("NO_NATIVE_FOCUS") || playerCount > 1)
		FocusMode.custom
	else
		FocusMode.native
