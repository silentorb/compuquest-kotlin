package compuquest.serving

import godot.Node
import godot.PackedScene
import godot.Resource
import godot.Spatial
import godot.core.Vector3
import godot.global.GD
import scripts.entities.actor.AttachCharacter
import scripts.world.slotGroup
import scripts.world.zoneGroup
import silentorb.mythic.godoting.*
import silentorb.mythic.randomly.Dice
import java.lang.Integer.min

fun populateZone(factions: Map<String, Resource>, bodyScene: PackedScene, dice: Dice, zone: Node) {
  tempCatch {
    val root = zone.getTree()?.root
    if (root != null) {
      val slots = findChildren(zone) { it.isInGroup(slotGroup) }
        .filterIsInstance<Spatial>()

      val faction = getString(zone, "faction")
      val factionResource = factions[faction]

      val creatures = getVariantArray<Resource>(factionResource, "creatures")
      if (creatures.any()) {
        val selectionMax = min(3, slots.size)
        val selection = dice.take(slots, selectionMax)
        for (slot in selection) {
          val body = bodyScene.instance() as Spatial
          body.translation = slot.globalTransform.origin + Vector3(0f, -0.5f, 0f)
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

fun loadFactions(): Map<String, Resource> =
  getVariantArray<Resource>(GD.load("res://world/factions/index.tres"), "resources")
    .associateBy { getResourceName(it)!! }

fun populateZones(dice: Dice, scene: Node) {
  tempCatch {
    val factions = loadFactions()
    val bodyScene = GD.load<PackedScene>("res://entities/actor/ActorBodyCapsule.tscn")!!
    val zones = scene.getTree()?.getNodesInGroup(zoneGroup)?.filterIsInstance<Spatial>() ?: listOf()
    for (zone in zones) {
      if (zone.get("populate") != false)
        populateZone(factions, bodyScene, dice, zone)
    }
  }
}
