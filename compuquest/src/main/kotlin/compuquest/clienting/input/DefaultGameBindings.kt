package compuquest.clienting.input

import compuquest.clienting.gui.Screens
import compuquest.simulation.input.Commands
import godot.GlobalConstants
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.haft.*

object DefaultBindings {

	val gamepadDeadZone = InputProcessor(InputProcessorType.deadzone, 0.25f)
	val gamepadLookScalar = InputProcessor(InputProcessorType.scale, 1.8f)

	fun keyboardGame() = mapOf(
		GlobalConstants.KEY_W to Commands.moveForward,
		GlobalConstants.KEY_A to Commands.moveLeft,
		GlobalConstants.KEY_D to Commands.moveRight,
		GlobalConstants.KEY_S to Commands.moveBackward,
		GlobalConstants.KEY_SPACE to Commands.jump,
		GlobalConstants.KEY_ESCAPE to AdvancedCommand(Commands.navigate, argument = Screens.mainMenu),
	) + if (getDebugBoolean("DEV_MODE"))
		mapOf(
			GlobalConstants.KEY_F5 to Commands.newGame,
			GlobalConstants.KEY_F11 to Commands.addPlayer,
			)
	else
		mapOf()

	fun keyboardUi() = mapOf(
		GlobalConstants.KEY_ESCAPE to Commands.menuBack,
	)

	fun mouseGame() = mapOf(
		GlobalConstants.BUTTON_LEFT to Commands.primaryAction,
		MouseChannels.x.toLong() to AdvancedCommand(
			Commands.lookX, processors = listOf(
				InputProcessor(InputProcessorType.scale, 0.8f),
			)
		),
		MouseChannels.y.toLong() to AdvancedCommand(
			Commands.lookY, processors = listOf(
				InputProcessor(InputProcessorType.scale, 0.8f),
			)
		),
	)

	fun mouseUi() = mapOf(
		GlobalConstants.BUTTON_LEFT to Commands.activate,
	)

	fun gamepadGame() = mapOf(
		GlobalConstants.JOY_START to AdvancedCommand(Commands.navigate, argument = Screens.mainMenu),
		GlobalConstants.JOY_AXIS_1 to AdvancedCommand(
			Commands.moveLengthwise, processors = listOf(
				gamepadDeadZone,
				InputProcessor(InputProcessorType.invert),
			)
		),
		GlobalConstants.JOY_AXIS_0 to AdvancedCommand(
			Commands.moveLateral, processors = listOf(
				gamepadDeadZone,
			)
		),
		GlobalConstants.JOY_AXIS_2 to AdvancedCommand(
			Commands.lookX, processors = listOf(
				gamepadDeadZone,
				gamepadLookScalar,
			)
		),
		GlobalConstants.JOY_AXIS_3 to AdvancedCommand(
			Commands.lookY, processors = listOf(
				gamepadDeadZone,
				gamepadLookScalar,
			)
		),
		GamepadChannels.JOY_XBOX_X to Commands.primaryAction,
		GamepadChannels.JOY_XBOX_A to Commands.jump,
	)

	fun gamepadUi() = mapOf(
		GamepadChannels.JOY_XBOX_X to Commands.menuBack,
		GamepadChannels.JOY_XBOX_B to Commands.menuBack,
		GamepadChannels.JOY_XBOX_A to Commands.activate,
	)
}

fun defaultKeyboardMouseInputBindings(): ContextBindings = mapOf(
	InputContexts.game to createBindings(InputDevices.keyboard, DefaultBindings.keyboardGame()) +
			createBindings(InputDevices.mouse, DefaultBindings.mouseGame()),
	InputContexts.ui to createBindings(InputDevices.keyboard, DefaultBindings.keyboardUi()) +
			createBindings(InputDevices.mouse, DefaultBindings.mouseUi()),
)

fun defaultGamepadInputBindings(): ContextBindings =
	mapOf(
		InputContexts.game to createBindings(InputDevices.gamepad, DefaultBindings.gamepadGame()),
		InputContexts.ui to createBindings(InputDevices.gamepad, DefaultBindings.gamepadUi()),
	)
