package compuquest.serving

import compuquest.simulation.definition.Definitions
import compuquest.simulation.general.World

data class GameState(
  val definitions: Definitions,
  val world: World,
)
