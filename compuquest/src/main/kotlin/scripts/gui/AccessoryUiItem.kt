package scripts.gui

import compuquest.simulation.characters.AccessoryDefinition
import godot.Button
import godot.annotation.RegisterClass

@RegisterClass
class AccessoryUiItem : Button() {
	var key: Any = -1
	var accessory: AccessoryDefinition? = null
}
