package silentorb.mythic.haft

import godot.GlobalConstants
import godot.Input

data class AxisCommands(
	val both: String,
	val negative: String,
	val positive: String,
)

fun getGamepadAxisState(gamepad: Int, scancode: Int): Float =
	Input.getJoyAxis(gamepad.toLong(), scancode.toLong()).toFloat()

fun getGamepadAxisState(bindings: Bindings, gamepad: Int, scancode: Int, bindingScancode: Int = scancode): Float =
	bindings.fold(0f) { a, binding ->
		if (binding.device == InputDevices.gamepad && binding.scancode == bindingScancode)
			a + applyInputProcessors(binding.processors, getGamepadAxisState(gamepad, scancode))
		else
			a
	}

fun isGamepadButtonPressed(bindings: Bindings, gamepad: Int, scancode: Int): Boolean {
	return if (scancode < 200) {
		val rawScancode = if (scancode >= gamepadButtonOffset)
			scancode - gamepadButtonOffset
		else
			scancode

		Input.isJoyButtonPressed(gamepad.toLong(), rawScancode.toLong())
	} else
		when (scancode) {
			GamepadChannels.JOY_LEFT_STICK_LEFT.toInt() ->
				getGamepadAxisState(
					bindings,
					gamepad,
					GlobalConstants.JOY_AXIS_0.toInt(),
					GamepadChannels.JOY_LEFT_STICK_LEFT.toInt()
				) < 0f
			GamepadChannels.JOY_LEFT_STICK_UP.toInt() ->
				getGamepadAxisState(
					bindings,
					gamepad,
					GlobalConstants.JOY_AXIS_1.toInt(),
					GamepadChannels.JOY_LEFT_STICK_UP.toInt()
				) < 0f
			GamepadChannels.JOY_LEFT_STICK_RIGHT.toInt() ->
				getGamepadAxisState(
					bindings,
					gamepad,
					GlobalConstants.JOY_AXIS_0.toInt(),
					GamepadChannels.JOY_LEFT_STICK_RIGHT.toInt()
				) > 0f
			GamepadChannels.JOY_LEFT_STICK_DOWN.toInt() ->
				getGamepadAxisState(
					bindings,
					gamepad,
					GlobalConstants.JOY_AXIS_1.toInt(),
					GamepadChannels.JOY_LEFT_STICK_DOWN.toInt()
				) > 0f

			GamepadChannels.JOY_RIGHT_STICK_LEFT.toInt() ->
				getGamepadAxisState(
					bindings,
					gamepad,
					GlobalConstants.JOY_AXIS_2.toInt(),
					GamepadChannels.JOY_RIGHT_STICK_LEFT.toInt()
				) < 0f
			GamepadChannels.JOY_RIGHT_STICK_UP.toInt() ->
				getGamepadAxisState(
					bindings,
					gamepad,
					GlobalConstants.JOY_AXIS_3.toInt(),
					GamepadChannels.JOY_RIGHT_STICK_UP.toInt()
				) < 0f
			GamepadChannels.JOY_RIGHT_STICK_RIGHT.toInt() ->
				getGamepadAxisState(
					bindings,
					gamepad,
					GlobalConstants.JOY_AXIS_2.toInt(),
					GamepadChannels.JOY_RIGHT_STICK_RIGHT.toInt()
				) > 0f
			GamepadChannels.JOY_RIGHT_STICK_DOWN.toInt() ->
				getGamepadAxisState(
					bindings,
					gamepad,
					GlobalConstants.JOY_AXIS_3.toInt(),
					GamepadChannels.JOY_RIGHT_STICK_DOWN.toInt()
				) > 0f
			else -> false
		}
}

fun isButtonPressed(bindings: Bindings, device: Int, scancode: Int, gamepad: Int): Boolean =
	when (device) {
		InputDevices.keyboard -> Input.isKeyPressed(scancode.toLong())
		InputDevices.mouse -> Input.isMouseButtonPressed(scancode.toLong())
		else -> if (gamepad != -1)
			isGamepadButtonPressed(bindings, gamepad, scancode)
		else
			false
	}

fun isButtonPressed(bindings: Bindings, gamepad: Int, command: String): Boolean =
	bindings
		.any { binding ->
			binding.command == command && isButtonPressed(bindings, binding.device, binding.scancode, gamepad)
		}

fun isButtonJustPressed(bindings: Bindings, device: Int, scancode: Int, gamepad: Int = -1): Boolean {
	val isPressed = isButtonPressed(bindings, device, scancode, gamepad)
	val adjustedDevice = if (gamepad > -1)
		device + gamepad
	else
		device

	return updateButtonDown(adjustedDevice, scancode, isPressed)
}

fun getButtonState(bindings: Bindings, device: Int, scancode: Int, gamepad: Int = -1): RelativeButtonState {
	val isPressed = isButtonPressed(bindings, device, scancode, gamepad)
	val adjustedDevice = if (gamepad > -1)
		device + gamepad
	else
		device

	return updateButtonState(adjustedDevice, scancode, isPressed)
}

fun isButtonJustReleased(bindings: Bindings, device: Int, scancode: Int, gamepad: Int = -1): Boolean {
	val isPressed = isButtonPressed(bindings, device, scancode, gamepad)
	val adjustedDevice = if (gamepad > -1)
		device + gamepad
	else
		device

	return updateButtonUp(adjustedDevice, scancode, isPressed)
}

fun isGamepadButtonJustPressed(bindings: Bindings, gamepad: Int, scancode: Int): Boolean {
	val isPressed = isGamepadButtonPressed(bindings, gamepad, scancode)
	val adjustedDevice = InputDevices.gamepad + gamepad
	return updateButtonDown(adjustedDevice, scancode, isPressed)
}

fun getButtonState(bindings: Bindings, gamepad: Int, command: String): RelativeButtonState {
	var state = RelativeButtonState.unknown
	for (binding in bindings) {
		if (binding.command == command) {
			state = getButtonState(bindings, binding.device, binding.scancode, gamepad)
			if (state == RelativeButtonState.justReleased || state == RelativeButtonState.justPressed)
				return state
		}
	}
	return state
}

fun isButtonJustPressed(bindings: Bindings, gamepad: Int, command: String): Boolean =
	bindings
		.any { binding ->
			binding.command == command && isButtonJustPressed(bindings, binding.device, binding.scancode, gamepad)
		}

fun isButtonJustReleased(bindings: Bindings, gamepad: Int, command: String): Boolean =
	bindings
		.any { binding ->
			binding.command == command && isButtonJustReleased(bindings, binding.device, binding.scancode, gamepad)
		}

fun getJustPressedBindings(bindings: Bindings, gamepad: Int, command: String): Bindings =
	bindings
		.filter { binding ->
			binding.command == command && isButtonJustPressed(bindings, binding.device, binding.scancode, gamepad)
		}

fun getAxisState(
	bindings: Bindings,
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
					getGamepadAxisState(gamepad, scancode)
				else
					0f
			}
		}
		commands.negative -> {
			if (isButtonPressed(bindings, device, scancode, gamepad))
				-1f
			else
				0f
		}
		commands.positive -> {
			if (isButtonPressed(bindings, device, scancode, gamepad))
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
		var value = getAxisState(bindings, binding, gamepad, commands)
		value = applyInputProcessors(binding.processors, value)
		result += value
	}
	return result
}
