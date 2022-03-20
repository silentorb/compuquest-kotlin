package scripts.gui

import godot.Control
import godot.Viewport
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.variantArrayOf

@RegisterClass
class ChildViewport : Viewport() {

	var previousFocus: Control? = null

	@RegisterFunction
	fun on_control_exited() {
		previousFocus?.disconnect("tree_exited", this, "on_control_exited")
		previousFocus = null
	}

	@RegisterFunction
	fun on_focus_changed(control: Control) {
		previousFocus?.notification(Control.NOTIFICATION_FOCUS_EXIT, true)
		previousFocus = control
		control.connect("tree_exited", this, "on_control_exited")
	}

	@RegisterFunction
	fun on_exiting_tree() {
		// Godot raises a soft error when attempting to remove a node from a group it is not inside.
		// Due to the workaround of removing the viewport from the "_viewports" group to support local multiplier UI,
		// the viewport is temporarily added back to the group before being removed from the group
		// The removing code is: https://github.com/godotengine/godot/blob/3.4.2-stable/scene/main/viewport.cpp#L360
		addToGroup("_viewports")
	}

	@RegisterFunction
	override fun _ready() {
		connect("gui_focus_changed", this, "on_focus_changed")
		connect("tree_exiting", this, "on_exiting_tree")
	}
}
