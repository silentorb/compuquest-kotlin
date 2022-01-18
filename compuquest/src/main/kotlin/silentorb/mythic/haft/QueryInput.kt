package silentorb.mythic.haft

import godot.Input

fun isButtonPressed(device: Int, scancode: Int): Boolean =
	when (device) {
		InputDevices.keyboard -> Input.isKeyPressed(scancode.toLong())
		InputDevices.mouse -> Input.isMouseButtonPressed(scancode.toLong())
		else -> Input.isJoyButtonPressed(device.toLong(), scancode.toLong())
	}

fun isButtonPressed(bindings: Bindings, command: String): Boolean =
	bindings
		.any { binding ->
			binding.command == command && isButtonPressed(binding.device, binding.scancode)
		}

fun getAxisState(bindings: Bindings, bothCommand: String, negativeCommand: String, positiveCommand: String): Float {
	var result = 0f
	for (binding in bindings) {
		val device = binding.device
		val scancode = binding.scancode

		result += when (binding.command) {
			bothCommand -> {
				when (device) {
					InputDevices.mouse -> when (scancode) {
						0 -> globalMouseOffset.x.toFloat()
						1 -> globalMouseOffset.y.toFloat()
						else -> 0f
					}
					InputDevices.keyboard -> 0f
					else -> Input.getJoyAxis(device.toLong(), scancode.toLong()).toFloat()
				}
			}
			negativeCommand -> {
				if (isButtonPressed(device, scancode))
					-1f
				else
					0f
			}
			positiveCommand -> {
				if (isButtonPressed(device, scancode))
					1f
				else
					0f
			}
			else -> 0f
		}
	}
	return result
}
