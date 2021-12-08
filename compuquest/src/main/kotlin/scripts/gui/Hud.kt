package scripts.gui

import compuquest.clienting.getPlayerMenuStack
import compuquest.clienting.gui.syncGuiToState
import compuquest.simulation.combat.damageEvent
import compuquest.simulation.general.isPlayerDead
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.global.GD
import scripts.Global
import silentorb.mythic.godoting.tempCatch

@RegisterClass
class Hud : Control() {
	var slot: Node? = null
	var interact: Control? = null
	var debugText: Label? = null
	var lastMenu: Any? = null
	var lowerThird: Control? = null
	var painOverlay: Panel? = null
	var damageWeight: Float = 0f

	@RegisterFunction
	override fun _ready() {
		slot = findNode("slot")
		interact = findNode("interact") as? Control
		debugText = findNode("debug") as? Label
		lowerThird = findNode("lower-third") as? Control
		painOverlay = findNode("pain-overlay") as? Panel
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		tempCatch {
			val client = Global.instance?.client
//		if (client != null) {
//			lowerThird?.visible = client.options.ui.showHud
//		}
			val player = Global.getPlayer()
			val canInteractWith = player?.value?.canInteractWith
			interact!!.visible = canInteractWith != null
			debugText?.text = Global.instance?.debugText ?: ""
			val localSlot = slot
			if (client != null && player != null) {
				val menuStack = getPlayerMenuStack(client, player.key)

				if (localSlot != null) {
					syncGuiToState(localSlot, player.key, Global.world!!, lastMenu, menuStack)
					lastMenu = menuStack.lastOrNull()
				}
			}

			val overlay = painOverlay
			if (overlay != null) {
				val isPainOverlayVisible = isPlayerDead(Global.world?.deck)
				overlay.visible = isPainOverlayVisible
				if (isPainOverlayVisible) {
					val style = overlay.getStylebox("panel") as StyleBoxFlat
					style.bgColor = Color(1f, 0f, 0f, 0.6f)
				} else {
					val world = Global.world
					if (player != null && world != null && world.previousEvents.any { event -> event.target == player.key && event.type == damageEvent }) {
						damageWeight = 0.4f
					}
					if (damageWeight > 0f) {
						overlay.visible = true
						val style = overlay.getStylebox("panel") as StyleBoxFlat
						damageWeight = GD.lerp(damageWeight, 0f, 0.1f)
						style.bgColor = Color(1f, 0f, 0f, damageWeight)
					}
				}
			}
		}
	}
}
