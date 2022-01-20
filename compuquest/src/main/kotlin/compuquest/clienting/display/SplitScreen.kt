package compuquest.clienting.display

import compuquest.clienting.Client
import compuquest.clienting.gui.addHud
import compuquest.simulation.general.World
import godot.*
import scripts.gui.Hud
import silentorb.mythic.ent.Id
import silentorb.mythic.godoting.findChildrenOfType

data class SplitViewport(
	val player: Id,
	val viewport: Viewport,
	val rigCamera: Camera,
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

fun newSplitScreenViewport(godotWorld: godot.World, player: Id, rigCamera: Camera): SplitViewport {
	val viewport = Viewport()
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

fun rebuildSplitScreenViewports(world: World, oldViewports: SplitViewports): SplitViewports {
	val players = world.deck.players.keys
	val root = getRootViewport(world.scene)!!

	val viewportsContainer = oldViewports
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
			initializeSingleViewportMode(root, players.first())
		)
	} else {
		root.usage = Viewport.Usage.USAGE_2D.id
		val godotWorld = root.world!!
		val viewports = players.map {
			val body = world.bodies[it]!!
			val rigCamera = findChildrenOfType<Camera>(body).first()
			newSplitScreenViewport(godotWorld, it, rigCamera)
		}
		val mounted = viewports.map { mountViewport(it.viewport) }
		arrangeViewports(root, mounted)
		viewports
	}
}

// Due to one of the myriad of nonsensical Godot design decisions, cameras have to be inside a viewport instead of
// viewports having a field that references which camera it uses.
// To make this work with splitscreen, a dummy camera exists inside each viewport.
// The viewport camera is synced with the character rig camera.
fun syncViewportCameras(viewports: SplitViewports) {
	for (viewport in viewports) {
		viewport.viewportCamera.transform = viewport.rigCamera.globalTransform
	}
}

fun updateSplitScreenViewports(world: World, client: Client): SplitViewports {
	val viewports = if (world.deck.players.size == client.viewports.size)
		client.viewports
	else
		rebuildSplitScreenViewports(world, client.viewports)

	// No camera syncing is needed for a single viewport
	if (viewports.size > 1) {
		syncViewportCameras(viewports)
	}

	return viewports
}
