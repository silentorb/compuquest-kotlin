package compuquest.definition

import compuquest.simulation.definition.Definitions

fun newDefinitions() =
  Definitions(
    accessories = accessoryDefinitions(),
    characters = staticCharacterDefinitions,
  )
