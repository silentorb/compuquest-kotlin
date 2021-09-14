package compuquest.simulation.general

import compuquest.simulation.definition.Definitions
import compuquest.simulation.definition.Zone
import godot.Node
import godot.Spatial
import silentorb.mythic.ent.Id
import silentorb.mythic.ent.SharedNextId
import silentorb.mythic.randomly.Dice

data class World(
  val definitions: Definitions,
  val nextId: SharedNextId,
  val bodies: Map<Id, Spatial>,
  val zones: Map<Key, Zone>,
  val deck: Deck = Deck(),
  val dice: Dice,
  val scene: Node? = null,
  val step: Long, // With an update rate of 60 frames per second, this variable can safely track 48745201446 years
)
