package compuquest.definition

import compuquest.simulation.definition.Definitions

fun newDefinitions() =
  Definitions(
    zones = defineZones(),
  )
