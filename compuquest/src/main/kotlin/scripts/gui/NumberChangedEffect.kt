package scripts.gui

import godot.Label
import godot.core.Color
import scripts.Global
import silentorb.mythic.godoting.instantiateScene

fun numberChangedEffect(label: Label, value: Int, previous: Int?) {
  if (previous != null && value != previous && Global.getPlayer()?.value?.isPlaying == true) {
    val root = label.getTree()?.root
    if (root != null) {
      val diff = value - previous
//    val position = label.rectGlobalPosition
      val k = label.get("rect_global_position")
      val floatingText = instantiateScene<Label>("res://gui/effects/NumberChanged.tscn")!!
      floatingText.text = diff.toString()
//    floatingText.setPosition(Vector2(position.x + label.rectSize.x / 2f, position.y))
      floatingText.set("original", label)
      val color = if (diff > 0)
        Color(0, 1, 0)
      else
        Color(1, 0, 0)

      floatingText.set("color", color)
//    floatingText.setSize(Vector2(label.rectSize.x, floatingText.rectSize.y))
      root.addChild(floatingText)
    }
  }
}
