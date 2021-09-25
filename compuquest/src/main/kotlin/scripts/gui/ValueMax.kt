package scripts.gui

import godot.Node
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty

@RegisterClass
class ValueMax : Node() {
  @Export
  @RegisterProperty
  var value: Int = 0
	set(newValue) {
	  if (field != newValue) {
		field = newValue
		valueLabel?.value = newValue
	  }
	}

  @Export
  @RegisterProperty
  var max: Int = 0
	set(newValue) {
	  if (field != newValue) {
		field = newValue
		maxLabel?.value = newValue
	  }
	}

  var valueLabel: IntLabel? = null
  var maxLabel: IntLabel? = null

  @RegisterFunction
  override fun _ready() {
	valueLabel = findNode("value") as? IntLabel
	maxLabel = findNode("max") as? IntLabel
	valueLabel?.value = value
	maxLabel?.value = max
  }
}
