package compuquest.serving

import silentorb.mythic.godoting.findChildren
import silentorb.mythic.godoting.getVariantArray
import silentorb.mythic.godoting.tempCatch
import godot.Node
import godot.PackedScene
import godot.Resource
import godot.Spatial
import godot.core.VariantArray
import godot.core.Vector3
import godot.global.GD
import scripts.entities.actor.AttachCharacter
import scripts.world.slotGroup
import scripts.world.zoneGroup
import silentorb.mythic.randomly.Dice
import java.lang.Integer.min

val baseNamePattern = Regex("([\\w \\-]+)\\.\\w+$")
fun getFactionKey(resource: Resource): String? =
  baseNamePattern.find(resource.resourcePath)?.groupValues?.drop(1)?.firstOrNull()

fun populateZone(bodyScene: PackedScene, dice: Dice, zone: Node) {
  tempCatch {
    val root = zone.getTree()?.root
    if (root != null) {
      val slots = findChildren(zone) { it.isInGroup(slotGroup) }
        .filterIsInstance<Spatial>()

      val factionResource = zone.get("faction") as? Resource
      val faction = if (factionResource != null)
        getFactionKey(factionResource)
      else
        null

      val creatures = getVariantArray<Resource>("creatures", factionResource)
      if (creatures.any() && faction != null) {
        val selectionMax = min(3, slots.size)
        val selection = dice.take(slots, selectionMax)
        for (slot in selection) {
          val body = bodyScene.instance() as Spatial
          body.translation = slot.globalTransform.origin + Vector3(0f, 1f, 0f)
          val creature = dice.takeOne(creatures)
          val attachCharacter = AttachCharacter()
          attachCharacter.creature = creature
          attachCharacter.faction = faction
          attachCharacter.healthValue = (creature.get("health") as Long).toInt()
          body.addChild(attachCharacter)
          root.addChild(body)
        }
      }
    }
  }
}

fun populateZones(dice: Dice, scene: Node) {
  tempCatch {
    val bodyScene = GD.load<PackedScene>("res://entities/actor/ActorBodyCapsule.tscn")!!
    val zones = scene.getTree()?.getNodesInGroup(zoneGroup)?.filterIsInstance<Spatial>() ?: listOf()
    for (zone in zones) {
      if (zone.get("populate") != false)
        populateZone(bodyScene, dice, zone)
    }
  }
}
