package compuquest.clienting.input

import compuquest.simulation.input.Commands
import godot.GlobalConstants

val gameGamepadBindings = mapOf(
	GlobalConstants.JOY_START to Commands.menu,
	GlobalConstants.JOY_AXIS_0 to Commands.moveHorizontal,
	GlobalConstants.JOY_AXIS_1 to Commands.moveVertical,
	GlobalConstants.JOY_AXIS_2 to Commands.lookHorizontal,
	GlobalConstants.JOY_AXIS_3 to Commands.lookVertical,
	GlobalConstants.JOY_XBOX_X to Commands.primaryAction,
	GlobalConstants.JOY_XBOX_A to Commands.jump,
)

fun defaultKeyboardGameBindings() = mapOf(
	GlobalConstants.KEY_W to Commands.moveUp,
	GlobalConstants.KEY_A to Commands.moveLeft,
	GlobalConstants.KEY_D to Commands.moveRight,
	GlobalConstants.KEY_S to Commands.moveDown,
	GlobalConstants.KEY_SPACE to Commands.jump,
	GlobalConstants.KEY_ESCAPE to Commands.menu,
)
