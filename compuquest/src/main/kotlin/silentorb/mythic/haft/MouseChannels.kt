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
	const val JOY_SELECT = GlobalConstants.JOY_SELECT + gamepadButtonOffset
	const val JOY_START = GlobalConstants.JOY_START + gamepadButtonOffset
	const val JOY_LEFT_TRIGGER = GlobalConstants.JOY_L2 + gamepadButtonOffset
	const val JOY_RIGHT_TRIGGER = GlobalConstants.JOY_R2 + gamepadButtonOffset
	const val JOY_LEFT_STICK_UP = 200L
	const val JOY_LEFT_STICK_DOWN = 201L
	const val JOY_LEFT_STICK_LEFT = 202L
	const val JOY_LEFT_STICK_RIGHT = 203L
	const val JOY_RIGHT_STICK_UP = 204L
	const val JOY_RIGHT_STICK_DOWN = 205L
	const val JOY_RIGHT_STICK_LEFT = 206L
	const val JOY_RIGHT_STICK_RIGHT = 207L
}
