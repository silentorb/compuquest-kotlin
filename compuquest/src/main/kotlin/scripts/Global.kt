package scripts

import compuquest.app.newGame
import compuquest.clienting.*
import compuquest.clienting.display.applyDisplayOptions
import compuquest.clienting.input.updateMouseMode
import compuquest.definition.newDefinitions
import compuquest.simulation.definition.Factions
import silentorb.mythic.godoting.tempCatch
import compuquest.simulation.input.Commands
import compuquest.simulation.general.*
import compuquest.simulation.general.World
import compuquest.simulation.input.PlayerInputs
import silentorb.mythic.happening.Event
import compuquest.simulation.updating.updateWorld
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector2
import silentorb.mythic.debugging.checkDotEnvChanged
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.ent.Id
import silentorb.mythic.haft.globalMouseOffset
import silentorb.mythic.happening.Events

enum class InitMode {
	delayInit,
	readyInitOrInitialized,
	delayRestart,
	readyRestart,
//	debugNone,
}

@RegisterClass
class Global : Node() {
	var worlds: List<World> = listOf()
	val definitions = newDefinitions()
	var client: Client? = null

	//	var sceneNode: Spatial? = null
	val partyUi: Boolean = false

	@RegisterProperty
	var debugText: String = ""

	@RegisterFunction
	fun getCharacterDepiction(type: String): String =
		definitions.characters[type]!!.depiction

	@RegisterFunction
	fun getCharacterFrame(type: String): Int =
		definitions.characters[type]!!.frame

	companion object {
		var instance: Global? = null
		private var serverEventQueue: MutableList<Event> = mutableListOf()
		private var clientEventQueue: MutableList<Event> = mutableListOf()

		fun addEvent(event: Event) {
			serverEventQueue.add(event)
			clientEventQueue.add(event)
		}

		fun addEvents(events: Collection<Event>) {
			serverEventQueue.addAll(events)
			clientEventQueue.addAll(events)
		}

		fun addHand(hand: Hand) {
			addEvent(newHandEvent(hand))
		}

		fun addHands(hands: Collection<Hand>) {
			for (hand in hands) {
				addHand(hand)
			}
		}

		fun addPlayerEvent(type: String, player: Id, value: Any? = null) {
			addEvent((Event(type, player, value)))
		}

		val world: World?
			get() = instance?.worlds?.lastOrNull()
	}

	init {
		instance = this
	}

	var initMode: InitMode = InitMode.delayInit

	fun restartGame() {
		val tree = getTree()
		val root = tree?.root
		if (root != null) {
//      val world = worlds.lastOrNull()
//      if (world != null) {
//        for (body in world.bodies.values) {
//          body.queueFree()
//        }
//        for (sprites in world.sprites.values) {
//          sprites.queueFree()
//        }
//      }

//			if (partyUi) {
//				// For no discernible reason, the current scene needs to be a direct child of the tree root
//				val scene = sceneNode!!
//				scene.getParent()!!.removeChild(scene)
//				tree.root!!.addChild(scene)
//			}

			// Wait until the frame has finished processing and the queued nodes are freed before
			// continuing with the restarting process
			initMode = InitMode.delayRestart
		}
	}

	fun updateServerEvents(): Events {
		val events = serverEventQueue.toList()
		serverEventQueue.clear()
		return events
	}

	fun updateClientEvents(): Events {
		val events = clientEventQueue.toList()
		clientEventQueue.clear()
		return events
	}

	fun updateWorlds(events: Events, input: PlayerInputs, worlds: List<World>, delta: Float) {
		if (events.any { it.type == Commands.newGame }) {
			restartGame()
		} else {
			val nextWorld = updateWorld(events, input, delta, worlds)
			this.worlds = worlds.plus(nextWorld).takeLast(2)
		}
	}

	fun newGameWorld(): World? {
		val event = InputEventKey()
		event.scancode = GlobalConstants.KEY_J
		InputMap.actionAddEvent("toggleHud", event)
		return tempCatch {
			val tree = getTree()
			val root = tree?.root
			if (root != null) {
//        newGame(root, definitions)
				val scene = root.getChildren().filterIsInstance<Spatial>().lastOrNull()
//        if (partyUi) {
//          val viewport = findChildren(root) { it.name == "viewport3d" }.firstOrNull() as? Viewport
//          val rootViewport = root as? Viewport
//          if (scene != null && viewport != null && rootViewport != null) {
//            root.removeChild(scene)
//            viewport.addChild(scene)
//            viewport.world = rootViewport.world
//            sceneNode = scene
//            newGame(scene, definitions)
//          } else
//            null
//        }
//        else
				val scenario = Scenario(
					name = "Dev Scenario",
					defaultPlayerFaction = Factions.player,
					playerRespawning = getDebugBoolean("PLAYER_RESPAWN"),
				)
				newGame(scene!!, scenario, definitions)
			} else
				null
		}
	}

	fun updateClient(delta: Float) {
		if (initMode == InitMode.readyInitOrInitialized) {
			val localClient = client ?: newClient()
			val clientEvents = eventsFromClient(localClient, world)
			addEvents(clientEvents)
			val nextClient = updateClient(worlds.lastOrNull(), updateClientEvents(), delta, localClient)
			checkSaveOptions(localClient, nextClient)
			client = nextClient
			updateMouseMode(nextClient)
			globalMouseOffset = Vector2.ZERO
		}
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		updateClient(delta.toFloat())
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		if (!Engine.editorHint) {
			if (getDebugBoolean("WATCH_DOT_ENV"))
				checkDotEnvChanged()

			debugText = ""
			tempCatch {
				if (client == null) {
					client = newClient()
				}
				if (Input.isActionJustReleased("quit"))
					getTree()!!.quit()

				val localWorlds = worlds
				when (initMode) {

					InitMode.delayRestart -> {
						val tree = getTree()!!
//						tree.currentScene = sceneNode
						tree.reloadCurrentScene()
						initMode = InitMode.readyRestart
					}

					InitMode.readyRestart -> {
						val world = newGameWorld()
						// This needs to happen *after* the scene is reloaded
						if (world != null) {
							worlds = listOf(world)
							client = restartClient(client!!)
							initMode = InitMode.readyInitOrInitialized
						}
					}

					InitMode.readyInitOrInitialized -> {
						if (localWorlds.none()) {
							val world = newGameWorld()
							if (world != null) {
								worlds = listOf(world)
								applyDisplayOptions(client!!.options.display)
							}
						} else {
							val events = updateServerEvents()
							updateWorlds(events, client!!.playerInputs, localWorlds, delta.toFloat())
						}
					}
					InitMode.delayInit -> {
						// Delay initialization one frame to ensure nodes deleted during _ready are completely removed.
						// This should not be needed and is either a Godot design flaw or bug
						initMode = InitMode.readyInitOrInitialized
					}
				}
			}
		}
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		// Certain cases of non-captured mouse mode are resulting in event.relative containing absolute coordinates.
		// This seems like a Godot bug.
		// It may be getting caused when switching between captured and non-captured mode, which should be returning (0,0)
		// but may be diffing absolute and relative coordinates.
		// The only occurrances run into so far were when starting a new game.
		if (event is InputEventMouseMotion && Input.getMouseMode() == Input.MouseMode.MOUSE_MODE_CAPTURED) {
			globalMouseOffset = event.relative
		}
	}

	override fun _onDestroy() {
		// Need to release any references to Godot objects or there will be a memory leak
		worlds = listOf()
	}
}

fun debugLog(message: String) {
	val previous = Global.instance?.debugText ?: ""
	Global.instance?.debugText = listOf(previous, message).joinToString("\n")
}
