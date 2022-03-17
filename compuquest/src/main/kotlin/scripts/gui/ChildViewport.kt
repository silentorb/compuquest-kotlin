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
	fun on_focus_changed(control: Control) {
		previousFocus?.notification(Control.NOTIFICATION_FOCUS_EXIT, true)
		previousFocus = control
	}

	@RegisterFunction
	override fun _ready() {
		connect("gui_focus_changed", this, "on_focus_changed")
	}
}
