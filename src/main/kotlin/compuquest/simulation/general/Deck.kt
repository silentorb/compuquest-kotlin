package compuquest.simulation.general

import compuquest.simulation.intellect.Spirit

data class Deck(
  val characters: Table<Character>,
  val factions: Table<Faction>,
  val spirits: Table<Spirit>,
)
