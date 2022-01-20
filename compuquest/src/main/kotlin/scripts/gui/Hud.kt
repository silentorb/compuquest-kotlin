package scripts.gui

import compuquest.clienting.getPlayerMenuStack
import compuquest.clienting.gui.syncGuiToState
import compuquest.simulation.characters.isCharacterAlive
import compuquest.simulation.combat.damageEvent
import compuquest.simulation.general.playerRespawnTime
import compuquest.simulation.updating.simulationFps
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.global.GD
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.tempCatch

const val respawnCountdownDelay = 1 * simulationFps

@RegisterClass
class Hud : Control() {
	var slot: Node? = null
	var interact: Label? = null
	var respawnCountdown: Label? = null
	var debugText: Label? = null
	var lastMenu: Any? = null
	var lowerThird: Control? = null
	var painOverlay: Panel? = null
	var damageWeight: Float = 0f
	var actor: Id = 0L

	@RegisterFunction
	override fun _ready() {
		slot = findNode("slot")
		interact = findNode("interact") as? Label
		respawnCountdown = findNode("respawn-countdown") as? Label
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
			val world = Global.world
			val player = world?.deck?.players?.getOrDefault(actor, null)
			if (player != null) {
				val canInteractWith = player.canInteractWith
				interact!!.visible = canInteractWith != null
				debugText?.text = Global.instance?.debugText ?: ""
				val localSlot = slot
				if (client != null) {
					val menuStack = getPlayerMenuStack(client, actor)

					if (localSlot != null) {
						syncGuiToState(localSlot, actor, Global.world!!, lastMenu, menuStack)
						lastMenu = menuStack.lastOrNull()
					}
				}

				val countdown = respawnCountdown!!
				if (player.respawnTimer > respawnCountdownDelay) {
					val remainingTime = (playerRespawnTime - player.respawnTimer) / simulationFps + 1
					if (remainingTime > 0) {
						countdown.visible = true
						countdown.text = remainingTime.toString()
					} else {
						countdown.visible = false
					}
				} else {
					countdown.visible = false
				}

				val overlay = painOverlay
				if (overlay != null) {
					val deck = world.deck
					val isPainOverlayVisible = !isCharacterAlive(deck, actor)
					overlay.visible = isPainOverlayVisible
					if (isPainOverlayVisible) {
						val style = overlay.getStylebox("panel") as StyleBoxFlat
						style.bgColor = Color(1f, 0f, 0f, 0.6f)
					} else {
						if (world.previousEvents.any { event -> event.target == actor && event.type == damageEvent }) {
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
}
