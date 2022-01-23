package silentorb.mythic.haft

import godot.core.Vector2

var globalMouseOffset = Vector2.ZERO

//data class InputEvent(
//	val device: Int,
//	val index: Int,
//	val value: Float
//)

typealias ButtonPressMap = MutableSet<Pair<Int, Int>>

object ButtonPresses {
	var previous: ButtonPressMap = mutableSetOf()
	var next: ButtonPressMap = mutableSetOf()
}

fun updateButtonPressHistory() {
	val old = ButtonPresses.previous
	ButtonPresses.previous = ButtonPresses.next
	old.clear()
	ButtonPresses.next = old
}

fun wasButtonDown(device: Int, index: Int): Boolean =
	ButtonPresses.previous.contains(device to index)

fun setButtonDown(device: Int, index: Int, isPressed: Boolean) {
	if (isPressed) {
		ButtonPresses.next += Pair(device, index)
	}
}

fun updateButtonDown(device: Int, scancode: Int, isPressed: Boolean): Boolean {
	setButtonDown(device, scancode, isPressed)
	return !wasButtonDown(device, scancode) && isPressed
}
