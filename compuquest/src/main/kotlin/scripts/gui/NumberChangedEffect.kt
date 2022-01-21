package scripts.gui

import godot.Label
import godot.core.Color
import godot.core.Vector2
import scripts.Global
import silentorb.mythic.godoting.instantiateScene

fun numberChangedEffect(label: Label, value: Int, previous: Int?) {
  if (previous != null && value != previous) {
    val root = label.getTree()?.root
    if (root != null) {
      val diff = value - previous
      val position = label.rectGlobalPosition
      val floatingText = instantiateScene<Label>("res://gui/effects/NumberChanged.tscn")!!
      floatingText.text = diff.toString()
      floatingText.setPosition(position)
      val color = if (diff > 0)
        Color(0, 1, 0)
      else
        Color(1, 0, 0)

      floatingText.set("color", color)
      root.addChild(floatingText)
    }
  }
}
