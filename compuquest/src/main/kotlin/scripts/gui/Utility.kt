package scripts.gui

import godot.Label

fun newLabel(value: String): Label {
  val label = Label()
  label.text = value
  return label
}
