package silentorb.mythic.haft

import godot.GlobalConstants

object MouseChannels {
	const val x = 10
	const val y = 11
}

// In one of its endless mistakes, Godot has conflicting Gamepad scancodes.
// This is a workaround to prevent collision.
const val gamepadButtonOffset = 100

object GamepadChannels {
	const val JOY_XBOX_X = GlobalConstants.JOY_XBOX_X + gamepadButtonOffset
	const val JOY_XBOX_Y = GlobalConstants.JOY_XBOX_Y + gamepadButtonOffset
	const val JOY_XBOX_A = GlobalConstants.JOY_XBOX_A + gamepadButtonOffset
	const val JOY_XBOX_B = GlobalConstants.JOY_XBOX_B + gamepadButtonOffset
}
