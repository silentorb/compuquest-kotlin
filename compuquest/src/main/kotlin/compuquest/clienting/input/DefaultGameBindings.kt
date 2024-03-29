package compuquest.clienting.input

import compuquest.clienting.gui.Screens
import compuquest.simulation.input.Commands
import godot.GlobalConstants
import silentorb.mythic.debugging.getDebugBoolean
import silentorb.mythic.haft.*

object DefaultBindings {

	val gamepadDeadZone = InputProcessor(InputProcessorType.deadzone, 0.25f)
	val gamepadLookScalarX = InputProcessor(InputProcessorType.scale, 1.8f)
	val gamepadLookScalarY = InputProcessor(InputProcessorType.scale, 1.2f)

	fun keyboardGame() = mapOf(
		GlobalConstants.KEY_W to Commands.moveForward,
		GlobalConstants.KEY_A to Commands.moveLeft,
		GlobalConstants.KEY_D to Commands.moveRight,
		GlobalConstants.KEY_S to Commands.moveBackward,
		GlobalConstants.KEY_SPACE to Commands.mobilityAction,
		GlobalConstants.KEY_E to Commands.interact,
		GlobalConstants.KEY_Q to Commands.utilityAction,
		GlobalConstants.KEY_ESCAPE to AdvancedCommand(Commands.navigate, argument = Screens.mainMenu),
	) + if (getDebugBoolean("DEV_MODE"))
		mapOf(
			GlobalConstants.KEY_F5 to Commands.newGame,
			GlobalConstants.KEY_F11 to Commands.addPlayer,
			GlobalConstants.KEY_CONTROL to Commands.crouch,
			GlobalConstants.KEY_KP_ADD to Commands.nextLevel,
			GlobalConstants.KEY_C to AdvancedCommand(Commands.navigate, argument = Screens.characterInfo),
		)
	else
		mapOf()

	fun keyboardUi() = mapOf(
		GlobalConstants.KEY_ESCAPE to Commands.menuBack,
		GlobalConstants.KEY_LEFT to Commands.moveLeft,
		GlobalConstants.KEY_RIGHT to Commands.moveRight,
		GlobalConstants.KEY_UP to Commands.moveUp,
		GlobalConstants.KEY_DOWN to Commands.moveDown,
		GlobalConstants.KEY_ENTER to Commands.activate,
		GlobalConstants.KEY_KP_ENTER to Commands.activate,
		GlobalConstants.KEY_SPACE to Commands.activate,
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

//	fun mouseUi() = mapOf(
//		GlobalConstants.BUTTON_LEFT to Commands.activate,
//	)

	fun gamepadGame() = mapOf(
		GamepadChannels.JOY_START to AdvancedCommand(Commands.navigate, argument = Screens.mainMenu),
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
				gamepadLookScalarX,
			)
		),
		GlobalConstants.JOY_AXIS_3 to AdvancedCommand(
			Commands.lookY, processors = listOf(
				gamepadDeadZone,
				gamepadLookScalarY,
			)
		),
		GamepadChannels.JOY_START to AdvancedCommand(Commands.navigate, argument = Screens.mainMenu),
		GamepadChannels.JOY_SELECT to AdvancedCommand(Commands.navigate, argument = Screens.characterInfo),
		GamepadChannels.JOY_XBOX_X to Commands.primaryAction,
		GamepadChannels.JOY_XBOX_Y to Commands.utilityAction,
		GamepadChannels.JOY_XBOX_B to Commands.interact,
		GamepadChannels.JOY_RIGHT_TRIGGER to Commands.primaryAction,
		GamepadChannels.JOY_XBOX_A to Commands.mobilityAction,
//		GlobalConstants.JOY_DPAD_LEFT to Commands.previousAction,
//		GlobalConstants.JOY_DPAD_RIGHT to Commands.nextAction,
	)

	fun gamepadUi() = mapOf(
		GamepadChannels.JOY_XBOX_X to Commands.menuBack,
		GamepadChannels.JOY_XBOX_B to Commands.menuBack,
		GamepadChannels.JOY_XBOX_A to Commands.activate,
		GlobalConstants.JOY_DPAD_LEFT to Commands.moveLeft,
		GlobalConstants.JOY_DPAD_RIGHT to Commands.moveRight,
		GlobalConstants.JOY_DPAD_UP to Commands.moveUp,
		GlobalConstants.JOY_DPAD_DOWN to Commands.moveDown,
		GamepadChannels.JOY_LEFT_STICK_LEFT to AdvancedCommand(
			Commands.moveLeft, processors = listOf(gamepadDeadZone)
		),
		GamepadChannels.JOY_LEFT_STICK_RIGHT to AdvancedCommand(
			Commands.moveRight, processors = listOf(gamepadDeadZone)
		),
		GamepadChannels.JOY_LEFT_STICK_UP to AdvancedCommand(
			Commands.moveUp, processors = listOf(gamepadDeadZone)
		),
		GamepadChannels.JOY_LEFT_STICK_DOWN to AdvancedCommand(
			Commands.moveDown, processors = listOf(gamepadDeadZone)
		),
		GamepadChannels.JOY_RIGHT_STICK_LEFT to AdvancedCommand(
			Commands.moveLeft, processors = listOf(gamepadDeadZone)
		),
		GamepadChannels.JOY_RIGHT_STICK_RIGHT to AdvancedCommand(
			Commands.moveRight, processors = listOf(gamepadDeadZone)
		),
		GamepadChannels.JOY_RIGHT_STICK_UP to AdvancedCommand(
			Commands.moveUp, processors = listOf(gamepadDeadZone)
		),
		GamepadChannels.JOY_RIGHT_STICK_DOWN to AdvancedCommand(
			Commands.moveDown, processors = listOf(gamepadDeadZone)
		),
	)
}

fun defaultKeyboardMouseInputBindings(): ContextBindings = mapOf(
	InputContexts.game to createBindings(InputDevices.keyboard, DefaultBindings.keyboardGame()) +
			createBindings(InputDevices.mouse, DefaultBindings.mouseGame()),
	InputContexts.ui to createBindings(InputDevices.keyboard, DefaultBindings.keyboardUi())// +
//			createBindings(InputDevices.mouse, DefaultBindings.mouseUi()),
)

fun defaultGamepadInputBindings(): ContextBindings =
	mapOf(
		InputContexts.game to createBindings(InputDevices.gamepad, DefaultBindings.gamepadGame()),
		InputContexts.ui to createBindings(InputDevices.gamepad, DefaultBindings.gamepadUi()),
	)
