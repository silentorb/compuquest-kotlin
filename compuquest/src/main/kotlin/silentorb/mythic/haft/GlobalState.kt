package silentorb.mythic.haft

import godot.core.Vector2

var globalMouseOffset = Vector2.ZERO

//data class InputEvent(
//	val device: Int,
//	val index: Int,
//	val value: Float
//)

typealias ButtonPressState = Int

object ButtonPressStates {
	const val unpressed = 0
	const val pressed = 1
	const val unknown = 2
}

typealias ButtonPressMap = MutableMap<Pair<Int, Int>, ButtonPressState>

object ButtonPresses {
	var previous: ButtonPressMap = mutableMapOf()
	var next: ButtonPressMap = mutableMapOf()
}

fun updateButtonPressHistory() {
	val old = ButtonPresses.previous
	ButtonPresses.previous = ButtonPresses.next
	for (key in old.keys) {
		old[key] = ButtonPressStates.unknown
	}
	ButtonPresses.next = old
}

fun wasButtonDefinitelyUp(device: Int, index: Int): Boolean =
	ButtonPresses.previous[device to index] == ButtonPressStates.unpressed

fun wasButtonDefinitelyDown(device: Int, index: Int): Boolean =
	ButtonPresses.previous[device to index] == ButtonPressStates.pressed

fun setButtonDown(device: Int, index: Int, isPressed: Boolean) {
	ButtonPresses.next[Pair(device, index)] = if (isPressed)
		ButtonPressStates.pressed
	else
		ButtonPressStates.unpressed
}

fun updateButtonDown(device: Int, scancode: Int, isPressed: Boolean): Boolean {
	setButtonDown(device, scancode, isPressed)
	return wasButtonDefinitelyUp(device, scancode) && isPressed
}

fun updateButtonUp(device: Int, scancode: Int, isPressed: Boolean): Boolean {
	setButtonDown(device, scancode, isPressed)
	return wasButtonDefinitelyDown(device, scancode) && !isPressed
}
