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
	override fun _ready() {
		connect("gui_focus_changed", this, "on_focus_changed")
	}
}
