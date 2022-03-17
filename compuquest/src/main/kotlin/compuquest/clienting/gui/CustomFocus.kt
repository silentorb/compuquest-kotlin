package compuquest.clienting.gui

import compuquest.clienting.input.getPlayerBindings
import compuquest.clienting.input.uiMenuNavigationBindingMap
import compuquest.simulation.input.Commands
import godot.Control
import scripts.Global
import silentorb.mythic.ent.Id
import silentorb.mythic.haft.*

typealias FocusMap = Map<Id, Int>

fun wrapIndex(size: Int, index: Int): Int =
	(index + size) % size

fun updateCustomFocus(playerMap: PlayerMap, menuStacks: MenuStacks, input: InputState, focusMap: FocusMap): FocusMap =
	playerMap
		.mapNotNull { (actor, playerIndex) ->
			if (getFocusMode(playerMap.size) == FocusMode.custom || menuStacks[actor]?.any() != true) {
				val bindings = getPlayerBindings(input, actor, playerIndex)
				val menuScreen = Global.instance?.playerMenus?.getOrDefault(actor, null)
				if (bindings != null && menuScreen != null) {
					val gamepad = getPlayerGamepad(input, playerIndex)
					val itemCount = menuScreen.items.size
					actor to uiMenuNavigationBindingMap.keys.fold(focusMap[actor] ?: 0) { index, command ->
						val state = getButtonState(bindings, gamepad, command)
						val newIndex = if (state == RelativeButtonState.justReleased) {
							when (command) {
								Commands.moveUp -> wrapIndex(itemCount, index - 1)
								Commands.moveDown -> wrapIndex(itemCount, index + 1)
								else -> index
							}
						} else
							index

						// Throwing in some side-effects to an otherwise pure function.
						// Could be moved out later.
						if (newIndex != index) {
							(menuScreen.itemsContainer?.getChild(newIndex.toLong()) as? Control)?.grabFocus()
						}
						newIndex
					}
				} else
					null
			} else
				null
		}
		.associate { it }
