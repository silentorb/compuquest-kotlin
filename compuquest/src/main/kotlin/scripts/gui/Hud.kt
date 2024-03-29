package scripts.gui

import compuquest.clienting.Client
import compuquest.clienting.gui.getPlayerMenuStack
import compuquest.clienting.gui.syncGuiToState
import compuquest.simulation.characters.AccessorySlot
import compuquest.simulation.characters.canUse
import compuquest.simulation.characters.getAccessoryInSlot
import compuquest.simulation.characters.isCharacterAlive
import compuquest.simulation.combat.damageEvent
import compuquest.simulation.general.World
import compuquest.simulation.updating.simulationFps
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.global.GD
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.capitalize
import silentorb.mythic.godoting.tempCatch

const val respawnCountdownDelay = 1 * simulationFps

@RegisterClass
class Hud : Control() {
	var menuContainer: Node? = null
	var interact: Control? = null
	var interactLabel: Label? = null
	var respawnCountdown: Label? = null
	var debugText: Label? = null
	var lastMenu: Any? = null
	var lowerThird: Control? = null
	var painOverlay: Panel? = null
	var damageWeight: Float = 0f
	var actor: Id = 0L
	var utility: AnimatedSprite? = null
	var utilityFrame = -1
		set(value) {
			if (field != value) {
				field = value
				utility?.visible = value >= 0
				if (value > 0)
					utility?.frame = value.toLong()
			}
		}

	@RegisterFunction
	override fun _ready() {
		menuContainer = findNode("menus")
		interact = findNode("interact") as? Control
		interactLabel = findNode("interact-label") as? Label
		respawnCountdown = findNode("respawn-countdown") as? Label
		debugText = findNode("debug") as? Label
		lowerThird = findNode("lower-third") as? Control
		painOverlay = findNode("pain-overlay") as? Panel
		utility = findNode("utility") as? AnimatedSprite
		Global.instance!!.huds.add(this)
	}

	fun updateMenus(client: Client) {
		val localSlot = menuContainer
		if (localSlot != null) {
			val menuStack = getPlayerMenuStack(client, actor)
			syncGuiToState(localSlot, actor, Global.world!!, lastMenu, menuStack)
			lastMenu = menuStack.lastOrNull()
		}
	}

	fun updateOverlay(world: World, overlay: Panel) {
		val deck = world.deck

		// Don't show the pain overlay if the character is alive or the player doesn't have an associated character
		val isPainOverlayVisible = deck.characters[actor]?.isAlive == false
		overlay.visible = isPainOverlayVisible
		if (isPainOverlayVisible) {
			val style = overlay.getStylebox("panel") as StyleBoxFlat
			style.bgColor = Color(1f, 0f, 0f, 0.6f)
		} else {
			if (world.previousEvents.any { event -> event.target == actor && event.type == damageEvent }) {
				damageWeight = 0.4f
			}
			if (damageWeight > 0) {
				if (damageWeight > 0.001f) {
					overlay.visible = true
					val style = overlay.getStylebox("panel") as StyleBoxFlat
					damageWeight = GD.lerp(damageWeight, 0f, 0.1f)
					style.bgColor = Color(1f, 0f, 0f, damageWeight)
				} else {
					damageWeight = 0f
				}
			}
		}
	}

	fun updateRespawnCountdown(playerRespawnTime: Int, respawnTimer: Int) {
		val countdown = respawnCountdown!!
		if (respawnTimer > respawnCountdownDelay) {
			val remainingTime = (playerRespawnTime - respawnTimer) / simulationFps + 1
			if (remainingTime > 0) {
				countdown.visible = true
				countdown.text = remainingTime.toString()
			} else {
				countdown.visible = false
			}
		} else {
			countdown.visible = false
		}
	}

	fun updateHud() {
		tempCatch {
			val client = Global.instance?.client
			val world = Global.world
			val deck = world?.deck
			val player = deck?.players?.getOrDefault(actor, null)
			if (player != null) {
				val canInteractWith = player.canInteractWith
				interact!!.visible = canInteractWith != null
				if (canInteractWith != null) {
					interactLabel!!.text = capitalize(canInteractWith.action)
				}
				updateRespawnCountdown(world.scenario.playerRespawnTime, player.respawnTimer)
			}

			debugText?.text = Global.instance?.debugText ?: ""
			if (client != null) {
				updateMenus(client)
			}

			val overlay = painOverlay
			if (overlay != null && world != null) {
				updateOverlay(world, overlay)
			}

			if (deck != null) {
				val utilityAccessory = getAccessoryInSlot(deck, actor, AccessorySlot.utility)
				utilityFrame = if (utilityAccessory != null) {
					val rawEquippedFrame = utilityAccessory.definition.equippedFrame
					val equippedFrame = if (rawEquippedFrame == -1)
						22
					else
						rawEquippedFrame

					val adjustment = if (canUse(utilityAccessory))
						0
					else
						1

					equippedFrame + adjustment
				} else
					-1
			}
		}
	}

	override fun _onDestroy() {
		Global.instance!!.huds.remove(this)
	}
}
