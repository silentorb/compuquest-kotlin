package compuquest.simulation.general

import compuquest.simulation.definition.ZoneState
import godot.Spatial

data class World(
  val deck: Deck,
  val bodies: Map<Id, Spatial>,
  val player: Player,
  val zones: Map<Key, ZoneState>,
)
