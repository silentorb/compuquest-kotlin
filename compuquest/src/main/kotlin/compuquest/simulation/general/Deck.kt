package compuquest.simulation.general

import compuquest.simulation.intellect.Spirit

data class Deck(
  val accessories: Table<Accessory> = mapOf(),
  val characters: Table<Character> = mapOf(),
  val factions: Table<Faction> = mapOf(),
  val spirits: Table<Spirit> = mapOf(),
)
