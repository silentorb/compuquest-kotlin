package scripts

import compuquest.app.newGame
import compuquest.clienting.*
import compuquest.clienting.display.applyDisplayOptions
import compuquest.definition.newDefinitions
import silentorb.mythic.godoting.tempCatch
import compuquest.simulation.general.Player
import compuquest.simulation.general.World
import compuquest.simulation.general.getPlayer
import compuquest.simulation.input.Commands
import compuquest.clienting.input.gatherDefaultPlayerInput
import silentorb.mythic.happening.Event
import compuquest.simulation.updating.updateWorld
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import silentorb.mythic.debugging.checkDotEnvChanged
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.findChildren
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.happening.Events

enum class InitMode {
	delayInit,
	readyInitOrInitialized,
	delayRestart,
	readyRestart,
	debugNone,
}

@RegisterClass
class Global : Node() {
	var worlds: List<World> = listOf()
	val definitions = newDefinitions()
	var client: Client = newClient()
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
		private var eventQueue: MutableList<Event> = mutableListOf()

		fun addEvent(event: Event) {
			eventQueue.add(event)
		}

		fun addPlayerEvent(type: String, value: Any? = null) {
			val player = world?.deck?.players?.keys?.firstOrNull()
			addEvent((Event(type, player, value)))
		}

		val world: World?
			get() = instance?.worlds?.lastOrNull()

		fun getPlayer(): Map.Entry<Id, Player>? =
			getPlayer(world)

		fun getMenuStack() =
			instance!!.client.menuStack
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

	@RegisterFunction
	override fun _ready() {
		val hud = instantiateScene<Node>("res://gui/hud/Hud.tscn")!!
		hud.owner = getTree()!!.editedSceneRoot
		getParent()!!.callDeferred("add_child", hud)
	}

	fun updateEvents(): Events {
		val events = eventQueue.toList() + gatherDefaultPlayerInput(worlds.last())
		eventQueue.clear()
		return events
	}

	fun updateWorlds(events: Events, worlds: List<World>, delta: Float) {
		if (events.any { it.type == Commands.newGame }) {
			restartGame()
		} else {
			val nextWorld = updateWorld(events, delta, worlds)
			this.worlds = worlds.plus(nextWorld).takeLast(2)
		}
	}

	fun newGameWorld(): World? {
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
				newGame(scene!!, definitions)
			} else
				null
		}
	}

	@RegisterFunction
	override fun _physicsProcess(delta: Double) {
		if (!Engine.editorHint) {
			if (getDebugBoolean("WATCH_DOT_ENV"))
				checkDotEnvChanged()

			debugText = ""
			tempCatch {
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
							client = restartClient(client)
							initMode = InitMode.readyInitOrInitialized
						}
					}

					InitMode.readyInitOrInitialized -> {
						if (localWorlds.none()) {
							val world = newGameWorld()
							if (world != null) {
								worlds = listOf(world)
								applyDisplayOptions(client.options.display)
							}
						} else {
							val events = updateEvents()
							val previousClient = client
							val nextClient = updateClient(localWorlds.lastOrNull(), events, previousClient)
							val player = getPlayer()?.key
							val clientEvents = if (player != null)
								eventsFromClient(player, nextClient, previousClient)
							else
								listOf()

							client = nextClient
							updateWorlds(events + clientEvents, localWorlds, delta.toFloat())
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

	override fun _onDestroy() {
		// Need to release any references to Godot objects or there will be a memory leak
		worlds = listOf()
	}
}

fun debugLog(message: String) {
	val previous = Global.instance?.debugText ?: ""
	Global.instance?.debugText = listOf(previous, message).joinToString("\n")
}
