package compuquest.simulation.general

data class World(
  val deck: Deck,
  val player: Player,
  val zones: Map<Key, ZoneState>,
)
