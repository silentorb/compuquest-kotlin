package compuquest.clienting.gui

import scripts.Global
import silentorb.mythic.ent.Id

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
