package silentorb.mythic.haft

import godot.InputEvent
import godot.InputEventJoypadButton
import godot.InputEventKey

fun bindingToGodotInputEvent(binding: Binding, gamepad: Int): InputEvent? =
	when (binding.device) {
		InputDevices.keyboard -> {
			val event = InputEventKey()
			event.scancode = binding.scancode.toLong()
			event
		}
		InputDevices.gamepad -> {
			if (gamepad != -1) {
				val event = InputEventJoypadButton()
				event.buttonIndex = (binding.scancode - gamepadButtonOffset).toLong()
				event
			} else
				null
		}
		else -> null
	}
