package compuquest.serving

import compuquest.definition.newDefinitions
import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.Deck
import compuquest.simulation.general.Player
import compuquest.simulation.general.World

fun newWorld(player: Player): World =
  World(
    deck = Deck(),
    bodies = mapOf(),
    player = player,
    zones = mapOf(),
  )

fun newGameState(definitions: Definitions): GameState {
  val player = Player(0L)
  val world = newWorld(player)
  return GameState(
    definitions = definitions,
    world = world,
  )
}
