package compuquest.clienting.gui

import compuquest.clienting.input.getPlayerBindings
import compuquest.clienting.input.uiMenuNavigationBindingMap
import compuquest.simulation.input.Commands
import scripts.Global
import silentorb.mythic.ent.Key
import silentorb.mythic.haft.*

fun wrapIndex(size: Int, index: Int): Int =
	(index + size) % size

fun updateCustomInputHandlers(playerMap: PlayerMap, menuStacks: MenuStacks, input: InputState) {
	playerMap.forEach { (actor, playerIndex) ->
		if (getFocusMode(playerMap.size) == FocusMode.custom && menuStacks[actor]?.any() == true) {
			val bindings = getPlayerBindings(input, actor, playerIndex)
			val menuScreen = Global.instance?.playerMenus?.getOrDefault(actor, null)
			if (bindings != null && menuScreen != null) {
				val gamepad = getPlayerGamepad(input, playerIndex)
				menuScreen.applyInput(bindings, gamepad)
			}
		}
	}
}

fun getNewMenuFocusIndex(decrementCommand: Key, incrementCommand: Key): (Bindings, Int, Int, Int) -> Int =
	{ bindings, gamepad, itemCount, focusIndex ->
		uiMenuNavigationBindingMap.keys.fold(focusIndex) { index, command ->
			val state = getButtonState(bindings, gamepad, command)
			if (state == RelativeButtonState.justReleased) {
				when (command) {
					decrementCommand -> wrapIndex(itemCount, index - 1)
					incrementCommand -> wrapIndex(itemCount, index + 1)
					else -> index
				}
			} else
				index
		}
	}

val updateMenuFocusVertical = getNewMenuFocusIndex(Commands.moveUp, Commands.moveDown)
val updateMenuFocusHorizontal = getNewMenuFocusIndex(Commands.moveLeft, Commands.moveRight)
