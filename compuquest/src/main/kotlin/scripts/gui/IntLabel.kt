package scripts.gui

import godot.Label
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import silentorb.mythic.godoting.instantiateScene
import silentorb.mythic.godoting.tempCatch
import kotlin.math.max

@RegisterClass
class IntLabel : Label() {

  private var spawnFloatingText = false
  private var floatingTextGap = 0f

  @Export
  @RegisterProperty
  var value: Int = 0
    set(value) {
      if (field != value) {
        tempCatch {
          if (spawnFloatingText) {
            if (floatingTextGap <= 0f) {
              numberChangedEffect(this, value, field)
              floatingTextGap = 0.5f
            }
          }

          field = value
          text = value.toString()
        }
      }
    }

  @RegisterFunction
  override fun _ready() {
    spawnFloatingText = true
  }

  @RegisterFunction
  override fun _physicsProcess(delta: Double) {
    if (floatingTextGap > 0f) {
      floatingTextGap = max(0f, floatingTextGap - delta.toFloat())
    }
  }
}

fun newIntegerLabel(value: Int): IntLabel {
  return tempCatch {
    val label = instantiateScene<IntLabel>("res://gui/components/IntegerLabel.tscn")!!
    label.value = value
    label
  }!!
}
