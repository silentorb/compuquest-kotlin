package compuquest.clienting.multiplayer

import compuquest.clienting.gui.addHud
import compuquest.simulation.general.Deck
import compuquest.simulation.general.World
import godot.*
import scripts.gui.ChildViewport
import scripts.gui.Hud
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.findChildrenOfType
import silentorb.mythic.haft.PlayerMap

data class SplitViewport(
	val player: Id,
	val viewport: Viewport,
	val rigCamera: Camera?,
	val viewportCamera: Camera,
)

typealias SplitViewports = List<SplitViewport>

fun getRootViewport(node: Node): Viewport? =
	node.getTree()?.root

tailrec fun getViewportRoot(node: Node): Node? =
	when (val parent = node.getParent()) {
		null, is Viewport -> node
		else -> getViewportRoot(parent)
	}

fun initializeSingleViewportMode(root: Viewport, player: Id): SplitViewport {
	addHud(root, player)
	// This is a bit of a hack because the camera fields aren't used for single viewport mode
	// but it allows for normalized viewport state management.
	val camera = findChildrenOfType<Camera>(root).firstOrNull() ?: Camera()
	return SplitViewport(
		player = player,
		viewport = root,
		rigCamera = camera,
		viewportCamera = camera,
	)
}

fun newSplitScreenViewport(godotWorld: godot.World, player: Id, rigCamera: Camera?): SplitViewport {
	val viewport = ChildViewport()
	viewport.name = "player-viewport-$player"
	viewport.renderDirectToScreen = true // This is up to implementation details but should be more performant
	viewport.world = godotWorld

	val camera = Camera()
	camera.name = "camera"
	viewport.addChild(camera)
	addHud(viewport, player)
	return SplitViewport(
		player = player,
		viewport = viewport,
		rigCamera = rigCamera,
		viewportCamera = camera,
	)
}

fun mountViewport(viewport: Viewport): ViewportContainer {
	val container = ViewportContainer()
	container.name = "${viewport.name}-container"
	container.setAnchor(GlobalConstants.MARGIN_RIGHT, 1.0)
	container.setAnchor(GlobalConstants.MARGIN_BOTTOM, 1.0)
	container.sizeFlagsHorizontal = Control.SizeFlags.SIZE_EXPAND_FILL.id
	container.sizeFlagsVertical = Control.SizeFlags.SIZE_EXPAND_FILL.id
	// The stretch field is misnamed.
	// Setting stretch to true means the viewport will stretch to fit the container, not that the container will stretch.
	container.stretch = true
	container.addChild(viewport)
	return container
}

fun newViewportVBox(name: String): VBoxContainer {
	val vbox = VBoxContainer()
	vbox.sizeFlagsHorizontal = Control.SizeFlags.SIZE_EXPAND_FILL.id
	vbox.name = "right-vbox"
	return vbox
}


fun arrangeViewports(root: Viewport, viewports: List<ViewportContainer>) {
	val hbox = HBoxContainer()
	hbox.name = "viewports"
	hbox.setAnchor(GlobalConstants.MARGIN_RIGHT, 1.0)
	hbox.setAnchor(GlobalConstants.MARGIN_BOTTOM, 1.0)
	root.addChild(hbox)

	when (viewports.size) {
		2 -> {
			hbox.addChild(viewports[0])
			hbox.addChild(viewports[1])
		}
		3 -> {
			val rightVbox = newViewportVBox("right-vbox")
			hbox.addChild(viewports[0])
			hbox.addChild(rightVbox)
			rightVbox.addChild(viewports[1])
			rightVbox.addChild(viewports[2])
		}
		else -> {
			val leftVbox = newViewportVBox("left-vbox")
			val rightVbox = newViewportVBox("right-vbox")
			hbox.addChild(leftVbox)
			hbox.addChild(rightVbox)
			leftVbox.addChild(viewports[0])
			leftVbox.addChild(viewports[1])
			rightVbox.addChild(viewports[2])
			rightVbox.addChild(viewports[3])
		}
	}
}

// Even though Godot stores GUI focus per Viewport,
// when focus is changed Godot sends a message to all viewports to clear their focus.
// In order to support local multiplayer GUI, a workaround is to remove the child viewports
// from the "_viewports" group so they don't recieve the message to clear their focus.
// It looks like that group is mostly used for functionality only needed by the root
// viewport, and if a need ever does arise for the child viewports, code could be attached to
// either the root viewport or some other intermediate node that could forward the needed messages
// to the child viewports through some other group.
// The line sending the message is:
// * https://github.com/godotengine/godot/blob/3.4.2-stable/scene/main/viewport.cpp#L2674
// This function needs to be called after the viewport is added to the scene tree
// because it is not until then that it is added to the "_viewports" group.
fun finalizeChildViewport(viewport: Viewport) {
	viewport.removeFromGroup("_viewports")
}

fun getRigCamera(deck: Deck, actor: Id): Camera? {
	val body = deck.bodies[actor]
	return if (body != null)
		findChildrenOfType<Camera>(body).first()
	else
		null
}

fun rebuildSplitScreenViewports(world: World, players: PlayerMap, viewports: SplitViewports): SplitViewports {
	val root = getRootViewport(world.scene)!!

	val viewportsContainer = viewports
		.map { it.viewport }
		.minus(root)
		.map { getViewportRoot(it) }
		.firstOrNull()

	viewportsContainer?.queueFree()

	// When switching from single viewport to multiple viewports.
	// This could be placed further down in the block where player size > 1,
	// but is placed here to be always called to ensure the root hud
	// is always cleaned up in case of fringe issues.
	root.getChildren().filterIsInstance<Hud>().forEach { it.queueFree() }

	return if (players.size == 1) {
		root.usage = Viewport.Usage.USAGE_3D.id
		listOf(
			initializeSingleViewportMode(root, players.keys.first())
		)
	} else {
		root.usage = Viewport.Usage.USAGE_2D.id
		val godotWorld = root.world!!
		val deck = world.deck
		val nextViewports = players.entries
			.sortedBy { it.value }
			.map { (actor, _) ->
				val rigCamera = getRigCamera(deck, actor)
				newSplitScreenViewport(godotWorld, actor, rigCamera)
			}

		val mounted = nextViewports.map { mountViewport(it.viewport) }
		arrangeViewports(root, mounted)
		for (viewport in nextViewports) {
			finalizeChildViewport(viewport.viewport)
		}
		nextViewports
	}
}

// Due to one of the myriad of nonsensical Godot design decisions, cameras have to be inside a viewport instead of
// viewports having a field that references which camera it uses.
// To make this work with splitscreen, a dummy camera exists inside each viewport.
// The viewport camera is synced with the character rig camera.
fun syncViewportCameras(viewports: SplitViewports) {
	for (viewport in viewports) {
		val rigTransform = viewport.rigCamera?.globalTransform
		if (rigTransform != null) {
			viewport.viewportCamera.transform = viewport.rigCamera.globalTransform
		}
	}
}

fun updateSplitScreenViewports(world: World, playerMap: PlayerMap, viewports: SplitViewports): SplitViewports {
	val nextViewports = if (playerMap.size == viewports.size)
		if (viewports.any { it.rigCamera == null }) {
			viewports.map { viewport ->
				if (viewport.rigCamera == null) {
					viewport.copy(
						rigCamera = getRigCamera(world.deck, viewport.player)
					)
				} else
					viewport
			}
		} else
			viewports
	else
		rebuildSplitScreenViewports(world, playerMap, viewports)

	// No camera syncing is needed for a single viewport
	if (nextViewports.size > 1) {
		syncViewportCameras(nextViewports)
	}

	return nextViewports
}
