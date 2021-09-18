package compuquest.serving

import compuquest.godoting.findChildren
import compuquest.godoting.tempCatch
import godot.Node
import godot.PackedScene
import godot.Spatial
import godot.core.VariantArray
import godot.core.Vector3
import godot.global.GD
import scripts.world.slotGroup
import scripts.world.zoneGroup
import silentorb.mythic.randomly.Dice
import java.lang.Integer.min

fun populateZone(bodyScene: PackedScene, dice: Dice, zone: Node) {
  tempCatch {
    val root = zone.getTree()?.root
    if (root != null) {
      val slots = findChildren(zone) { it.isInGroup(slotGroup) }
        .filterIsInstance<Spatial>()

      val creatures = (zone.get("creatures") as? VariantArray<*> ?: listOf()).filterIsInstance<PackedScene>()
      if (creatures.any()) {
        val selectionMax = min(3, slots.size)
        val selection = dice.take(slots, selectionMax)
        for (slot in selection) {
          val body = bodyScene.instance() as Spatial
          body.translation = slot.globalTransform.origin + Vector3(0f, 1f, 0f)
          val characterScene = dice.takeOne(creatures)
          body.addChild(characterScene.instance() as Node)
          root.addChild(body)
        }
      }
    }
  }
}

fun populateZones(dice: Dice, scene: Node) {
  tempCatch {
    val bodyScene = GD.load<PackedScene>("res://entities/actor/ActorBody.tscn")!!
    val zones = scene.getTree()?.getNodesInGroup(zoneGroup)?.filterIsInstance<Spatial>() ?: listOf()
    for (zone in zones) {
      populateZone(bodyScene, dice, zone)
    }
  }
}
