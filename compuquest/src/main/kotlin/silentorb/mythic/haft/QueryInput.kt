package silentorb.mythic.haft

import godot.Input

data class AxisCommands(
	val both: String,
	val negative: String,
	val positive: String,
)

fun isButtonPressed(device: Int, scancode: Int, gamepad: Int): Boolean =
	when (device) {
		InputDevices.keyboard -> Input.isKeyPressed(scancode.toLong())
		InputDevices.mouse -> Input.isMouseButtonPressed(scancode.toLong())
		else -> if (gamepad != -1)
			Input.isJoyButtonPressed(gamepad.toLong(), (scancode - gamepadButtonOffset).toLong())
		else
			false
	}

fun isButtonPressed(bindings: Bindings, gamepad: Int, command: String): Boolean =
	bindings
		.any { binding ->
			binding.command == command && isButtonPressed(binding.device, binding.scancode, gamepad)
		}

fun isButtonJustPressed(device: Int, scancode: Int, gamepad: Int = -1): Boolean {
	val isPressed = isButtonPressed(device, scancode, gamepad)
	setButtonDown(device, scancode, isPressed)
	return !wasButtonDown(device, scancode) && isPressed
}

fun isButtonJustPressed(bindings: Bindings, gamepad: Int, command: String): Boolean =
	bindings
		.any { binding ->
			binding.command == command && isButtonJustPressed(binding.device, binding.scancode, gamepad)
		}

fun getJustPressedBindings(bindings: Bindings, gamepad: Int, command: String): Bindings =
	bindings
		.filter { binding ->
			binding.command == command && isButtonJustPressed(binding.device, binding.scancode, gamepad)
		}

fun getAxisState(
	binding: Binding,
	gamepad: Int,
	commands: AxisCommands,
): Float {
	val device = binding.device
	val scancode = binding.scancode

	return when (binding.command) {
		commands.both -> {
			when (device) {
				InputDevices.mouse -> when (scancode) {
					MouseChannels.x -> globalMouseOffset.x.toFloat()
					MouseChannels.y -> globalMouseOffset.y.toFloat()
					else -> 0f
				}
				InputDevices.keyboard -> 0f
				else -> if (gamepad != -1)
					Input.getJoyAxis(gamepad.toLong(), scancode.toLong()).toFloat()
				else
					0f
			}
		}
		commands.negative -> {
			if (isButtonPressed(device, scancode, gamepad))
				-1f
			else
				0f
		}
		commands.positive -> {
			if (isButtonPressed(device, scancode, gamepad))
				1f
			else
				0f
		}
		else -> 0f
	}
}

fun getAxisState(
	bindings: Bindings,
	gamepad: Int,
	commands: AxisCommands
): Float {
	var result = 0f
	for (binding in bindings) {
		var value = getAxisState(binding, gamepad, commands)

		for (processor in binding.processors) {
			value = applyInputProcessor(processor, value)
		}

		result += value
	}
	return result
}
