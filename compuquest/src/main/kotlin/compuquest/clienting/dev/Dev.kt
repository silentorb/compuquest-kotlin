package compuquest.clienting.dev

import compuquest.generation.general.getPointCell
import compuquest.simulation.definition.Factions
import compuquest.simulation.general.getPlayer
import compuquest.simulation.general.spawnNewPlayer
import godot.GlobalConstants
import scripts.Global
import scripts.setdebugText
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.haft.InputDevices
import silentorb.mythic.haft.isButtonJustPressed

fun updateDev() {
//	if (getDebugBoolean("DEV_MULTIPLAYER")) {
//		if (isButtonJustPressed(InputDevices.keyboard, GlobalConstants.KEY_F11.toInt())) {
//			Global.addEvents(spawnNewPlayer(Global.world!!, Factions.player))
//		}

	if (getDebugBoolean("DISPLAY_PLAYER_CELL")) {
		val world = Global.world
		val player = getPlayer(world)
		if (player != null) {
			val playerBody = world!!.deck.bodies[player.key]
			if (playerBody != null) {
				val cell = getPointCell(playerBody.globalTransform.origin)
				setdebugText(cell.minimalString())
			}
		}
	}
}
