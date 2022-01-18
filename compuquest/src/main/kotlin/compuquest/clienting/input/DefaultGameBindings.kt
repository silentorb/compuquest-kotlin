package compuquest.clienting.input

import compuquest.simulation.input.Commands
import godot.GlobalConstants
import silentorb.mythic.haft.*

val defaultGamepadDeadZone = InputProcessor(InputProcessorType.deadzone, 0.1f)
val defaultGamepadLookScalar = InputProcessor(InputProcessorType.scale, 1.8f)

fun defaultGamepadGameBindings() = mapOf(
	GlobalConstants.JOY_START to Commands.menu,
	GlobalConstants.JOY_AXIS_1 to CommandWithProcessors(Commands.moveLengthwise, listOf(
		defaultGamepadDeadZone,
		InputProcessor(InputProcessorType.invert),
	)),
	GlobalConstants.JOY_AXIS_0 to CommandWithProcessors(Commands.moveLateral, listOf(
		defaultGamepadDeadZone,
	)),
	GlobalConstants.JOY_AXIS_2 to CommandWithProcessors(Commands.lookX, listOf(
		defaultGamepadDeadZone,
		defaultGamepadLookScalar,
	)),
	GlobalConstants.JOY_AXIS_3 to CommandWithProcessors(Commands.lookY, listOf(
		defaultGamepadDeadZone,
		defaultGamepadLookScalar,
	)),
	GamepadChannels.JOY_XBOX_X to Commands.primaryAction,
	GamepadChannels.JOY_XBOX_A to Commands.jump,
)

fun defaultKeyboardGameBindings() = mapOf(
	GlobalConstants.KEY_W to Commands.moveForward,
	GlobalConstants.KEY_A to Commands.moveLeft,
	GlobalConstants.KEY_D to Commands.moveRight,
	GlobalConstants.KEY_S to Commands.moveBackward,
	GlobalConstants.KEY_SPACE to Commands.jump,
	GlobalConstants.KEY_ESCAPE to Commands.menu,
)

fun defaultMouseGameBindings() = mapOf(
	GlobalConstants.BUTTON_LEFT to Commands.primaryAction,
	MouseChannels.x.toLong() to CommandWithProcessors(Commands.lookX, listOf(
		InputProcessor(InputProcessorType.scale, 0.8f),
	)),
	MouseChannels.y.toLong() to CommandWithProcessors(Commands.lookY, listOf(
		InputProcessor(InputProcessorType.scale, 0.8f),
	)),
)

fun defaultInputProfile() =
	createBindings(InputDevices.keyboard, defaultKeyboardGameBindings()) +
			createBindings(InputDevices.mouse, defaultMouseGameBindings()) +
			createBindings(InputDevices.gamepad, defaultGamepadGameBindings())

