package compuquest.simulation.general

import compuquest.simulation.combat.Missile
import compuquest.simulation.intellect.Spirit
import silentorb.mythic.ent.Table

data class Deck(
  val players: Table<Player> = mapOf(),
  val accessories: Table<Accessory> = mapOf(),
  val characters: Table<Character> = mapOf(),
  val factions: Table<Faction> = mapOf(),
  val missiles: Table<Missile> = mapOf(),
  val spirits: Table<Spirit> = mapOf(),
)
